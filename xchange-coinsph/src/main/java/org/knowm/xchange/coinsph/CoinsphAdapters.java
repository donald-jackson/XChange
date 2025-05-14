package org.knowm.xchange.coinsph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.coinsph.dto.account.CoinsphAccount;
import org.knowm.xchange.coinsph.dto.account.CoinsphBalance;
import org.knowm.xchange.coinsph.dto.account.CoinsphTradeFee; // For trade fees
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphOrderBook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphOrderBookEntry;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphPublicTrade;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphTicker;
import org.knowm.xchange.coinsph.dto.meta.CoinsphExchangeInfo;
import org.knowm.xchange.coinsph.dto.meta.CoinsphSymbol;
import org.knowm.xchange.coinsph.dto.trade.CoinsphOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsphUserTrade; // For user trades
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
// import org.knowm.xchange.dto.account.DynamicTradingFees; // Class not found, replaced with Map<Instrument, Fee> // For adapting trade fees
import org.knowm.xchange.dto.account.Fee;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.OpenOrders; // For adapting open orders
import java.util.Map; // For DynamicTradingFees map
import java.util.HashMap; // For DynamicTradingFees map
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.instrument.Instrument;

public final class CoinsphAdapters {

  // Enum for Coins.ph specific order flags, if any (e.g. for quoteOrderQty)
  public enum CoinsphOrderFlags {
    QUOTE_ORDER_QTY // Used for market orders to specify the amount of quote asset to spend
  }

  private CoinsphAdapters() {
    // Private constructor for utility class
  }

  public static String toSymbol(CurrencyPair currencyPair) {
    if (currencyPair == null) {
      return null;
    }
    return currencyPair.getBase().getCurrencyCode() + currencyPair.getCounter().getCurrencyCode();
  }
  
  public static String toSymbol(Instrument instrument) {
    if (instrument == null) {
      return null;
    }
    return toSymbol(new CurrencyPair(instrument.getBase(), instrument.getCounter()));
  }

  public static CurrencyPair toCurrencyPair(String symbol) {
    if (symbol == null || symbol.length() < 6) { // Assuming symbols like BTCPHP (3+3 chars)
      return null; 
    }
    // This is a common way, but Coins.ph might have fixed length for base/quote
    // Need to confirm from their symbol list or exchangeInfo
    // For now, assume 3-char base and 3-char counter for common pairs like BTCPHP
    // Or, more robustly, iterate through known symbols from exchangeInfo
    // A simple split might not work for all cases (e.g. USDTPHP vs BTCUSDT)
    // Let's assume for now a common pattern or rely on exchangeInfo for parsing
    // For BTCPHP: base=BTC, counter=PHP
    // For ETHPHP: base=ETH, counter=PHP
    // For BTCUSDT: base=BTC, counter=USDT
    // A common approach is to check against known quote currencies.
    // For now, a placeholder, this needs to be robust.
    // A common pattern is that the last 3 or 4 chars are the quote.
    String counter = symbol.substring(symbol.length() - 3);
    String base = symbol.substring(0, symbol.length() - 3);
    if (counter.equals("SDT")) counter = "USDT"; // common case for USDT
    
    // A more robust way would be to use the list of symbols from exchangeInfo
    // to determine base and counter. For now, this is a simplification.
    return new CurrencyPair(base, counter);
  }

  public static AccountInfo adaptAccountInfo(CoinsphAccount coinsphAccount, String username) {
    List<Balance> balances = new ArrayList<>();
    if (coinsphAccount.getBalances() != null) {
      for (CoinsphBalance coinsphBalance : coinsphAccount.getBalances()) {
        balances.add(
            new Balance(
                new Currency(coinsphBalance.getAsset()),
                coinsphBalance.getTotal(), // total = free + locked
                coinsphBalance.getFree(),
                coinsphBalance.getLocked()));
      }
    }
    return new AccountInfo(username, Wallet.Builder.from(balances).build());
  }

  public static ExchangeMetaData adaptExchangeMetaData(CoinsphExchangeInfo exchangeInfo) {
    List<CoinsphSymbol> symbols = exchangeInfo.getSymbols();
    java.util.Map<Instrument, InstrumentMetaData> currencyPairs = new java.util.HashMap<>();
    java.util.Map<Currency, CurrencyMetaData> currencies = new java.util.HashMap<>();

    for (CoinsphSymbol symbol : symbols) {
      CurrencyPair pair = toCurrencyPair(symbol.getSymbol());
      if (pair == null) continue; // Skip if symbol parsing fails

      // TODO: Extract fee tiers, min/max amounts, price scale, quantity scale from symbol filters
      // For now, using defaults or placeholders
      InstrumentMetaData pairMetaData =
          new InstrumentMetaData.Builder()
              .tradingFee(null) // tradingFee
              .minimumAmount(null) // minimumAmount
              .maximumAmount(null) // maximumAmount
              .priceScale(symbol.getQuotePrecision()) // priceScale (assuming quotePrecision is price scale)
              .feeTiers(null) // feeTiers
              .build();
      currencyPairs.put(pair, pairMetaData);

      if (!currencies.containsKey(pair.getBase())) {
        currencies.put(pair.getBase(), new CurrencyMetaData(symbol.getBaseAssetPrecision(), null)); // scale, fee
      }
      if (!currencies.containsKey(pair.getCounter())) {
        currencies.put(pair.getCounter(), new CurrencyMetaData(symbol.getQuoteAssetPrecision(), null)); // scale, fee
      }
    }
    // TODO: Adapt rate limits from exchangeInfo.getRateLimits()
    return new ExchangeMetaData(currencyPairs, currencies, null, null, true);
  }

  public static Ticker adaptTicker(CoinsphTicker coinsphTicker) {
    if (coinsphTicker == null) {
      return null;
    }
    CurrencyPair pair = toCurrencyPair(coinsphTicker.getSymbol());
    return new Ticker.Builder()
        .instrument(pair)
        .open(coinsphTicker.getOpenPrice())
        .last(coinsphTicker.getLastPrice())
        .bid(coinsphTicker.getBidPrice())
        .ask(coinsphTicker.getAskPrice())
        .high(coinsphTicker.getHighPrice())
        .low(coinsphTicker.getLowPrice())
        .volume(coinsphTicker.getVolume())
        .quoteVolume(coinsphTicker.getQuoteVolume())
        .timestamp(new Date(coinsphTicker.getCloseTime())) // Using closeTime as timestamp
        .bidSize(coinsphTicker.getBidQty())
        .askSize(coinsphTicker.getAskQty())
        .percentageChange(coinsphTicker.getPriceChangePercent())
        .build();
  }

  public static List<Ticker> adaptTickers(List<CoinsphTicker> coinsphTickers) {
    if (coinsphTickers == null) {
      return Collections.emptyList();
    }
    return coinsphTickers.stream().map(CoinsphAdapters::adaptTicker).collect(Collectors.toList());
  }
  
  private static List<LimitOrder> adaptOrderBookList(List<CoinsphOrderBookEntry> entries, OrderType orderType, CurrencyPair currencyPair) {
    return entries.stream()
        .map(entry -> new LimitOrder(orderType, entry.getQuantity(), currencyPair, null, null, entry.getPrice()))
        .collect(Collectors.toList());
  }

  public static OrderBook adaptOrderBook(CoinsphOrderBook coinsphOrderBook, CurrencyPair currencyPair) {
    if (coinsphOrderBook == null) {
      return null;
    }
    List<LimitOrder> asks = adaptOrderBookList(coinsphOrderBook.getAsks(), OrderType.ASK, currencyPair);
    List<LimitOrder> bids = adaptOrderBookList(coinsphOrderBook.getBids(), OrderType.BID, currencyPair);
    return new OrderBook(new Date(coinsphOrderBook.getLastUpdateId()), asks, bids); // Assuming lastUpdateId is a timestamp
  }

  public static Trade adaptTrade(CoinsphPublicTrade coinsphTrade, CurrencyPair currencyPair) {
    return new Trade.Builder()
        .instrument(currencyPair)
        .originalAmount(coinsphTrade.getQty())
        .price(coinsphTrade.getPrice())
        .timestamp(new Date(coinsphTrade.getTime()))
        .id(String.valueOf(coinsphTrade.getId()))
        .type(coinsphTrade.isBuyerMaker() ? OrderType.ASK : OrderType.BID) // if buyer is maker, it was a sell order that got filled
        .build();
  }

  public static Trades adaptTrades(List<CoinsphPublicTrade> coinsphTrades, CurrencyPair currencyPair) {
    List<Trade> trades = coinsphTrades.stream()
        .map(trade -> adaptTrade(trade, currencyPair))
        .collect(Collectors.toList());
    // Coins.ph trades are sorted old to new. XChange expects new to old.
    Collections.reverse(trades); 
    // lastID can be used for pagination if needed, not directly part of Trades DTO
    long lastId = coinsphTrades.isEmpty() ? 0L : coinsphTrades.get(coinsphTrades.size() - 1).getId();
    return new Trades(trades, lastId, Trades.TradeSortType.SortByTimestamp);
  }

  public static String toSide(OrderType orderType) {
    switch (orderType) {
      case BID:
      case EXIT_ASK: // Assuming exit ask is a form of buy
        return "BUY";
      case ASK:
      case EXIT_BID: // Assuming exit bid is a form of sell
        return "SELL";
      default:
        throw new IllegalArgumentException("Unsupported order type: " + orderType);
    }
  }

  public static String toOrderType(Order order) {
    if (order instanceof LimitOrder) {
      return "LIMIT";
    } else if (order instanceof MarketOrder) {
      return "MARKET";
    }
    // TODO: Add STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT if Coins.ph supports
    throw new IllegalArgumentException("Unsupported order class: " + order.getClass().getName());
  }
  
  public static String toTimeInForce(Order.IOrderFlags flag) {
      if (flag == null) return "GTC"; // Default for Coins.ph if not specified
      if (flag == org.knowm.xchange.dto.Order.OrderFlags.IMMEDIATE_OR_CANCEL) {
        return "IOC";
      }
      if (flag == org.knowm.xchange.dto.Order.OrderFlags.FILL_OR_KILL) {
        return "FOK";
      }
      // Other flags are not directly mapped to Coins.ph timeInForce values.
      // GTC is a safe default if no specific TIF flag is matched.
      return "GTC";
  }

  public static OrderType adaptOrderType(String side) {
    switch (side.toUpperCase()) {
      case "BUY":
        return OrderType.BID;
      case "SELL":
        return OrderType.ASK;
      default:
        return null; // Or throw exception
    }
  }

  public static org.knowm.xchange.dto.Order adaptOrder(CoinsphOrder coinsphOrder) {
    if (coinsphOrder == null) {
      return null;
    }
    OrderType type = adaptOrderType(coinsphOrder.getSide());
    CurrencyPair pair = toCurrencyPair(coinsphOrder.getSymbol());
    Date timestamp = new Date(coinsphOrder.getTime()); // Or updateTime if more appropriate

    LimitOrder.Builder builder = new LimitOrder.Builder(type, pair)
        .id(String.valueOf(coinsphOrder.getOrderId()))
        .originalAmount(coinsphOrder.getOrigQty())
        .cumulativeAmount(coinsphOrder.getExecutedQty())
        .timestamp(timestamp)
        .orderStatus(adaptOrderStatus(coinsphOrder.getStatus()))
        .limitPrice(coinsphOrder.getPrice()) // Price is present for limit orders
        .averagePrice(coinsphOrder.getAvgPrice()) // If available, else calculate from fills
        .userReference(coinsphOrder.getClientOrderId());
        // TODO: Add fees if available in CoinsphOrder DTO

    return builder.build();
  }
  
  public static org.knowm.xchange.dto.Order.OrderStatus adaptOrderStatus(String coinsphStatus) {
    if (coinsphStatus == null) return org.knowm.xchange.dto.Order.OrderStatus.UNKNOWN;
    switch (coinsphStatus.toUpperCase()) {
      case "NEW":
        return org.knowm.xchange.dto.Order.OrderStatus.NEW;
      case "PARTIALLY_FILLED":
        return org.knowm.xchange.dto.Order.OrderStatus.PARTIALLY_FILLED;
      case "FILLED":
        return org.knowm.xchange.dto.Order.OrderStatus.FILLED;
      case "CANCELED": // Spelled with one L in Coins.ph docs
        return org.knowm.xchange.dto.Order.OrderStatus.CANCELED;
      case "PENDING_CANCEL":
        return org.knowm.xchange.dto.Order.OrderStatus.PENDING_CANCEL;
      case "REJECTED":
        return org.knowm.xchange.dto.Order.OrderStatus.REJECTED;
      case "EXPIRED":
        return org.knowm.xchange.dto.Order.OrderStatus.EXPIRED;
      default:
        return org.knowm.xchange.dto.Order.OrderStatus.UNKNOWN;
    }
  } // Added missing closing brace for the method adaptOrderStatus
public static OpenOrders adaptOpenOrders(List<CoinsphOrder> coinsphOrders) {
    List<LimitOrder> limitOrders = new ArrayList<>();
    List<Order> otherOrders = new ArrayList<>(); // For any non-limit orders if applicable

    if (coinsphOrders != null) {
      for (CoinsphOrder coinsphOrder : coinsphOrders) {
        Order order = adaptOrder(coinsphOrder);
        if (order instanceof LimitOrder) {
          limitOrders.add((LimitOrder) order);
        } else {
          // Market orders usually don't appear in open orders lists once (partially) filled
          // If Coins.ph can have other types of open orders, handle them here
          otherOrders.add(order); 
        }
      }
    }
    return new OpenOrders(limitOrders, otherOrders);
  }
  
public static Map<Instrument, Fee> adaptTradeFees(List<CoinsphTradeFee> coinsphTradeFees) {
    Map<Instrument, org.knowm.xchange.dto.Fee> fees = new HashMap<>();
    if (coinsphTradeFees != null) {
      for (CoinsphTradeFee fee : coinsphTradeFees) {
        Instrument instrument = toCurrencyPair(fee.getSymbol());
        if (instrument != null) {
          // Assuming maker and taker are distinct fees.
          // XChange Fee DTO takes one maker and one taker fee.
          fees.put(instrument, new org.knowm.xchange.dto.Fee(fee.getMakerCommission(), fee.getTakerCommission()));
        }
      }
    }
    return fees;
  }
  public static UserTrade adaptUserTrade(CoinsphUserTrade coinsphTrade) {
    if (coinsphTrade == null) {
      return null;
    }
    CurrencyPair currencyPair = toCurrencyPair(coinsphTrade.getSymbol());
    OrderType orderType = coinsphTrade.isBuyer() ? OrderType.BID : OrderType.ASK;
    Date timestamp = new Date(coinsphTrade.getTime());
    String tradeId = String.valueOf(coinsphTrade.getId());
    String orderId = String.valueOf(coinsphTrade.getOrderId());

    return new UserTrade.Builder()
        .instrument(currencyPair)
        .id(tradeId)
        .orderId(orderId)
        .timestamp(timestamp)
        .type(orderType)
        .price(coinsphTrade.getPrice())
        .originalAmount(coinsphTrade.getQty())
        .feeAmount(coinsphTrade.getCommission())
        .feeCurrency(Currency.getInstance(coinsphTrade.getCommissionAsset()))
        // .orderUserReference(null) // Not directly available in CoinsphUserTrade
        .build();
  }

  public static UserTrades adaptUserTrades(List<CoinsphUserTrade> coinsphTrades) {
    if (coinsphTrades == null) {
      return new UserTrades(Collections.emptyList(), Trades.TradeSortType.SortByTimestamp);
    }
    List<UserTrade> trades =
        coinsphTrades.stream()
            .map(CoinsphAdapters::adaptUserTrade)
            .collect(Collectors.toList());
    // Coins.ph /myTrades are sorted old to new by default (by tradeId).
    // XChange UserTrades are typically sorted by timestamp, newest first.
    // The list from stream().map() will preserve original order.
    // If sorting is needed (e.g. newest first), do it here.
    // For now, assume the order from API is acceptable or will be handled by caller.
    // The API docs say "If fromId (tradeId) is set, it will get id (tradeId) >= that fromId (tradeId).
    // Otherwise most recent trades are returned." This implies newest first if fromId is not used.
    // If fromId is used, it's oldest first from that ID.
    // XChange expects newest first. So if fromId is used, we might need to reverse.
    // However, the `Trades.TradeSortType.SortByTimestamp` implies the list should be sorted by time.
    // The API returns trades by tradeId, which generally correlates with time.
    // For now, let's not reverse, assuming "most recent" means newest first.
    return new UserTrades(trades, Trades.TradeSortType.SortByTimestamp);
  }

}