package info.bitrich.xchangestream.coinsph;

import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketAggTrade;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketDepth;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketExecutionReport;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketTicker; // Added for ticker adapter
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketBookTicker; // Added for book ticker adapter
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphTicker; // Added for ticker adapter
import org.knowm.xchange.dto.marketdata.Ticker; // Added for book ticker adapter
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.coinsph.CoinsphAdapters; // For base currency pair conversion
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;

public final class CoinsphStreamingAdapters {

  public static CurrencyPair getCurrencyPairFromSymbol(String symbol) {
    // Delegates to the existing adapter in xchange-coinsph for consistency
    return CoinsphAdapters.toCurrencyPair(symbol.toUpperCase());
  }

  private CoinsphStreamingAdapters() {}

  public static String getChannelName(CurrencyPair currencyPair, String streamType) {
    // e.g., btcusdt@depth, ethphp@aggTrade
    return CoinsphAdapters.toSymbol(currencyPair).toLowerCase() + "@" + streamType;
  }

  public static OrderBook adaptOrderBook(
      CoinsphWebSocketDepth wsDepth, CurrencyPair currencyPair) {
    List<LimitOrder> asks =
        wsDepth.getAsks().stream()
            .map(
                entry ->
                    new LimitOrder(
                        Order.OrderType.ASK, entry.get(1), currencyPair, null, null, entry.get(0)))
            .collect(Collectors.toList());
    List<LimitOrder> bids =
        wsDepth.getBids().stream()
            .map(
                entry ->
                    new LimitOrder(
                        Order.OrderType.BID, entry.get(1), currencyPair, null, null, entry.get(0)))
            .collect(Collectors.toList());
    // The WebSocket depth stream usually provides a lastUpdateId that can be used as a timestamp
    // or to manage order book consistency. Coins.ph uses 'u' (finalUpdateId).
    return new OrderBook(new Date(wsDepth.getEventTime()), asks, bids); // Using eventTime as book timestamp
  }

  public static Trade adaptTrade(CoinsphWebSocketAggTrade wsTrade) {
    return new Trade.Builder()
        .id(String.valueOf(wsTrade.getAggregateTradeId()))
        .instrument(CoinsphAdapters.toCurrencyPair(wsTrade.getSymbol().toUpperCase()))
        .price(wsTrade.getPrice())
        .originalAmount(wsTrade.getQuantity())
        .timestamp(new Date(wsTrade.getTradeTime()))
        .type(wsTrade.isBuyerMaker() ? Order.OrderType.SELL : Order.OrderType.BUY) // if buyer is maker, it was their sell order that got filled by a taker buy
        .build();
  }

  public static Order adaptOrder(CoinsphWebSocketExecutionReport report) {
    Order.OrderType type = CoinsphAdapters.adaptOrderType(report.getSide());
    CurrencyPair pair = CoinsphAdapters.toCurrencyPair(report.getSymbol().toUpperCase());
    Date timestamp = new Date(report.getTransactionTime()); // T field

    // Determine if it's a LimitOrder or MarketOrder based on Coins.ph order type 'o'
    // For now, assuming LimitOrder for simplicity if price is present.
    // This needs to align with how CoinsphAdapters.adaptOrder handles REST DTOs.
    Order.Builder builder;
    if ("LIMIT".equalsIgnoreCase(report.getOrderType()) || "LIMIT_MAKER".equalsIgnoreCase(report.getOrderType())) {
      builder = new LimitOrder.Builder(type, pair)
          .limitPrice(report.getOrderPrice());
    } else { // MARKET, STOP_LOSS, etc.
      // For non-limit orders, XChange DTOs might not store the original "price" field
      // if it's not a limit price.
      builder = new org.knowm.xchange.dto.trade.MarketOrder.Builder(type, pair);
    }
    
    builder
        .id(String.valueOf(report.getOrderId()))
        .originalAmount(report.getOrderQuantity())
        .cumulativeAmount(report.getCumulativeFilledQuantity())
        .timestamp(timestamp) // Use transaction time as primary timestamp for the update
        .orderStatus(CoinsphAdapters.adaptOrderStatus(report.getOrderStatus()))
        .userReference(report.getClientOrderId())
        .averagePrice(report.getLastExecutedPrice()); // Or calculate based on fills if available

    // Add fees if available (n = commissionAmount, N = commissionAsset)
    if (report.getCommissionAmount() != null && report.getCommissionAsset() != null) {
        builder.fee(report.getCommissionAmount()); 
        // Note: XChange Order DTO has one fee field. If commission asset differs from quote,
        // this might need more complex handling or a custom DTO extension.
    }
    
    return builder.build();
  }
  
public static List<Balance> adaptBalances(
      CoinsphWebSocketOutboundAccountPosition accountPosition) {
    return accountPosition.getBalances().stream()
        .map(
            wsBalance ->
                new Balance.Builder()
                    .currency(new org.knowm.xchange.currency.Currency(wsBalance.getAsset()))
                    .available(wsBalance.getFree())
                    .frozen(wsBalance.getLocked())
                    .total(wsBalance.getFree().add(wsBalance.getLocked()))
                    .timestamp(new Date(accountPosition.getEventTime())) 
                    // accountLastUpdateTime (u) could also be relevant if more precise timing is needed for the balance itself
                    .build())
        .collect(Collectors.toList());
  }
  public static UserTrade adaptUserTrade(CoinsphWebSocketExecutionReport report) {
    // This adapter is for when an "executionReport" event type is "TRADE" (x="TRADE")
    if (!"TRADE".equalsIgnoreCase(report.getExecutionType())) {
        return null; // Not a trade execution
    }

    Order.OrderType type = CoinsphAdapters.adaptOrderType(report.getSide());
    CurrencyPair pair = CoinsphAdapters.toCurrencyPair(report.getSymbol().toUpperCase());
    Date timestamp = new Date(report.getTransactionTime());

    return new UserTrade.Builder()
        .id(String.valueOf(report.getTradeId()))
        .orderId(String.valueOf(report.getOrderId()))
        .instrument(pair)
        .price(report.getLastExecutedPrice())
        .originalAmount(report.getLastExecutedQuantity())
        .timestamp(timestamp)
        .type(type) // The side of the order that generated this trade
        .feeAmount(report.getCommissionAmount())
        .feeCurrency(report.getCommissionAsset() != null ? new org.knowm.xchange.currency.Currency(report.getCommissionAsset()) : null)
        .orderUserReference(report.getClientOrderId())
        .build();
  }
  
  public static org.knowm.xchange.dto.marketdata.Ticker adaptTicker(
      info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketTicker wsTicker) {
    if (wsTicker == null) {
      return null;
    }
    // Create an instance of the REST DTO from the WebSocket DTO
    org.knowm.xchange.coinsph.dto.marketdata.CoinsphTicker restTicker =
        new org.knowm.xchange.coinsph.dto.marketdata.CoinsphTicker();
    restTicker.setSymbol(wsTicker.getSymbol());
    restTicker.setPriceChange(wsTicker.getPriceChange());
    restTicker.setPriceChangePercent(wsTicker.getPriceChangePercent());
    restTicker.setWeightedAvgPrice(wsTicker.getWeightedAvgPrice());
    restTicker.setPrevClosePrice(wsTicker.getPrevClosePrice());
    restTicker.setLastPrice(wsTicker.getLastPrice());
    restTicker.setLastQty(wsTicker.getLastQty());
    restTicker.setBidPrice(wsTicker.getBidPrice());
    restTicker.setBidQty(wsTicker.getBidQty());
    restTicker.setAskPrice(wsTicker.getAskPrice());
    restTicker.setAskQty(wsTicker.getAskQty());
    restTicker.setOpenPrice(wsTicker.getOpenPrice());
    restTicker.setHighPrice(wsTicker.getHighPrice());
    restTicker.setLowPrice(wsTicker.getLowPrice());
    restTicker.setVolume(wsTicker.getVolume());
    restTicker.setQuoteVolume(wsTicker.getQuoteVolume());
    restTicker.setOpenTime(wsTicker.getOpenTime());
    restTicker.setCloseTime(wsTicker.getCloseTime());
    restTicker.setFirstId(wsTicker.getFirstId());
    restTicker.setLastId(wsTicker.getLastId());
    restTicker.setCount(wsTicker.getCount());
    
    // Delegate to the existing adapter in xchange-coinsph
    return CoinsphAdapters.adaptTicker(restTicker);
  }

  public static org.knowm.xchange.dto.marketdata.Ticker adaptBookTicker(
      info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketBookTicker wsBookTicker) {
    if (wsBookTicker == null) {
      return null;
    }
    CurrencyPair currencyPair = getCurrencyPairFromSymbol(wsBookTicker.getSymbol());
    return new org.knowm.xchange.dto.marketdata.Ticker.Builder()
        .instrument(currencyPair)
        .bid(wsBookTicker.getBidPrice())
        .ask(wsBookTicker.getAskPrice())
        .bidSize(wsBookTicker.getBidQty())
        .askSize(wsBookTicker.getAskQty())
        .timestamp(new Date(wsBookTicker.getEventTime())) // Use event time from the message
        .build();
  }

  // Ticker and BookTicker adapters are now implemented above.
}