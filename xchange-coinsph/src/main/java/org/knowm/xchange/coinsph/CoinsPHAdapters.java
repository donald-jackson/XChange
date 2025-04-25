package org.knowm.xchange.coinsph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.trade.LimitOrder;

/**
 * Various adapters for converting from Coins.ph DTOs to XChange DTOs
 */
public final class CoinsPHAdapters {

  private CoinsPHAdapters() {}

  /**
   * Adapts a CoinsPHTicker24h to a Ticker object
   */
  public static Ticker adaptTicker(CoinsPHTicker24h ticker, CurrencyPair currencyPair) {
    return new Ticker.Builder()
        .currencyPair(currencyPair)
        .open(ticker.getOpenPrice())
        .last(ticker.getLastPrice())
        .bid(ticker.getBidPrice())
        .ask(ticker.getAskPrice())
        .high(ticker.getHighPrice())
        .low(ticker.getLowPrice())
        .volume(ticker.getVolume())
        .quoteVolume(ticker.getQuoteVolume())
        .timestamp(new Date(ticker.getCloseTime()))
        .build();
  }

  /**
   * Adapts a CoinsPHOrderbook to an OrderBook object
   */
  public static OrderBook adaptOrderBook(CoinsPHOrderbook orderbook, CurrencyPair currencyPair) {
    List<LimitOrder> bids = adaptLimitOrders(OrderType.BID, orderbook.getBids(), currencyPair);
    List<LimitOrder> asks = adaptLimitOrders(OrderType.ASK, orderbook.getAsks(), currencyPair);
    return new OrderBook(new Date(orderbook.getLastUpdateId()), asks, bids);
  }

  /**
   * Adapts a list of CoinsPHTrades to a Trades object
   */
  public static Trades adaptTrades(List<CoinsPHTrade> trades, CurrencyPair currencyPair) {
    List<Trade> tradeList = trades.stream()
        .map(trade -> adaptTrade(trade, currencyPair))
        .collect(Collectors.toList());
    return new Trades(tradeList, TradeSortType.SortByTimestamp);
  }

  /**
   * Adapts a CoinsPHTrade to a Trade object
   */
  public static Trade adaptTrade(CoinsPHTrade trade, CurrencyPair currencyPair) {
    OrderType type = trade.isBuyerMaker() ? OrderType.BID : OrderType.ASK;
    return new Trade.Builder()
        .type(type)
        .originalAmount(trade.getQty())
        .currencyPair(currencyPair)
        .price(trade.getPrice())
        .timestamp(new Date(trade.getTime()))
        .id(String.valueOf(trade.getId()))
        .build();
  }

  private static List<LimitOrder> adaptLimitOrders(
      OrderType orderType, List<List<BigDecimal>> orders, CurrencyPair currencyPair) {
    List<LimitOrder> limitOrders = new ArrayList<>();
    for (List<BigDecimal> order : orders) {
      if (order.size() == 2) {
        limitOrders.add(
            new LimitOrder(orderType, order.get(1), currencyPair, null, null, order.get(0)));
      }
    }
    return limitOrders;
  }
}