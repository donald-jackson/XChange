package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.coinsph.CoinsphAdapters;
import org.knowm.xchange.coinsph.CoinsphExchange;
import org.knowm.xchange.coinsph.dto.CoinsphException;
import org.knowm.xchange.coinsph.dto.trade.CoinsphNewOrderRequest; // For placing new orders
import org.knowm.xchange.coinsph.dto.trade.CoinsphOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce; // For order flags
import org.knowm.xchange.coinsph.dto.trade.CoinsphUserTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.StopOrder; // For stop orders
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
// import org.knowm.xchange.service.trade.params.TradeHistoryParams; // For trade history

public class CoinsphTradeServiceRaw extends CoinsphBaseService {

  protected CoinsphTradeServiceRaw(
      CoinsphExchange exchange, ResilienceRegistries resilienceRegistries) {
    super(exchange, resilienceRegistries);
  }

  public List<CoinsphOrder> getCoinsphOpenOrders(CurrencyPair currencyPair)
      throws IOException, CoinsphException {
    return decorateApiCall(
            () ->
                coinsphAuthenticated.getOpenOrders(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    currencyPair != null ? CoinsphAdapters.toSymbol(currencyPair) : null,
                    exchange.getRecvWindow()))
        // .withRetry(retry("openOrders"))
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public CoinsphOrder placeCoinsphMarketOrder(MarketOrder marketOrder)
      throws IOException, CoinsphException {
    String symbol = CoinsphAdapters.toSymbol(marketOrder.getCurrencyPair());
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderSide side = CoinsphAdapters.toSide(marketOrder.getType());
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType type = org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType.MARKET; // Explicitly MARKET

    BigDecimal quantity = null;
    BigDecimal quoteOrderQty = null;

    if (marketOrder.hasFlag(CoinsphAdapters.CoinsphOrderFlags.QUOTE_ORDER_QTY)) {
      quoteOrderQty = marketOrder.getOriginalAmount();
    } else {
      quantity = marketOrder.getOriginalAmount();
    }
    
    String newClientOrderId = marketOrder.getUserReference();
    Long recvWindow = exchange.getRecvWindow();
    // timeInForce, price, stopPrice are null for basic MARKET orders

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    symbol,
                    side,
                    type,
                    null, // timeInForce
                    quantity,
                    quoteOrderQty,
                    null, // price
                    newClientOrderId,
                    null, // stopPrice
                    recvWindow,
                    timestampFactory,
                    signatureCreator))
        // .withRetry(retry("newOrder"))
        // .withRateLimiter(rateLimiter(ORDERS_RATE_LIMITER))
        .call();
  }

  public CoinsphOrder placeCoinsphLimitOrder(LimitOrder limitOrder)
      throws IOException, CoinsphException {
    String symbol = CoinsphAdapters.toSymbol(limitOrder.getCurrencyPair());
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderSide side = CoinsphAdapters.toSide(limitOrder.getType());
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType type = org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType.LIMIT; // Explicitly LIMIT

    org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce timeInForce = org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce.GTC; // Default
    for (Order.IOrderFlags flag : limitOrder.getOrderFlags()) {
      if (flag instanceof org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) {
        timeInForce = (org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) flag;
        break;
      }
    }
    
    BigDecimal quantity = limitOrder.getOriginalAmount();
    BigDecimal price = limitOrder.getLimitPrice();
    String newClientOrderId = limitOrder.getUserReference();
    Long recvWindow = exchange.getRecvWindow();
    // quoteOrderQty and stopPrice are null for basic LIMIT orders

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    symbol,
                    side,
                    type,
                    timeInForce,
                    quantity,
                    null, // quoteOrderQty
                    price,
                    newClientOrderId,
                    null, // stopPrice
                    recvWindow,
                    timestampFactory,
                    signatureCreator))
        // .withRetry(retry("newOrder"))
        // .withRateLimiter(rateLimiter(ORDERS_RATE_LIMITER))
        .call();
  }

  public CoinsphOrder placeCoinsphStopOrder(org.knowm.xchange.dto.trade.StopOrder stopOrder)
      throws IOException, CoinsphException {
    String symbol = CoinsphAdapters.toSymbol(stopOrder.getCurrencyPair());
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderSide side = CoinsphAdapters.toSide(stopOrder.getType());
    
    org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType type;
    BigDecimal price = null; // Limit price for _LIMIT variants
    org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce timeInForce = null;

    // Infer type: STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT
    // Defaulting to STOP_LOSS variants. User can use flags for TAKE_PROFIT.
    // TODO: Add flag handling for TAKE_PROFIT vs STOP_LOSS selection.
    if (stopOrder.getLimitPrice() != null) {
        type = org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType.STOP_LOSS_LIMIT;
        price = stopOrder.getLimitPrice();
        timeInForce = org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce.GTC; // Default for limit part
        for (Order.IOrderFlags flag : stopOrder.getOrderFlags()) {
          if (flag instanceof org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) {
            timeInForce = (org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) flag;
            break;
          }
        }
    } else {
        type = org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType.STOP_LOSS;
        // timeInForce is generally null for market-triggering stop orders
    }

    BigDecimal quantity = null;
    BigDecimal quoteOrderQty = null;
    if (stopOrder.hasFlag(CoinsphAdapters.CoinsphOrderFlags.QUOTE_ORDER_QTY) && stopOrder.getLimitPrice() == null) {
      quoteOrderQty = stopOrder.getOriginalAmount();
    } else {
      quantity = stopOrder.getOriginalAmount();
    }

    BigDecimal stopPriceValue = stopOrder.getStopPrice();
    String newClientOrderId = stopOrder.getUserReference();
    Long recvWindow = exchange.getRecvWindow();

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    symbol,
                    side,
                    type,
                    timeInForce,
                    quantity,
                    quoteOrderQty,
                    price, // This is the limit price for _LIMIT variants
                    newClientOrderId,
                    stopPriceValue, // This is the stopPrice
                    recvWindow,
                    timestampFactory,
                    signatureCreator))
        // .withRetry(retry("newOrder"))
        // .withRateLimiter(rateLimiter(ORDERS_RATE_LIMITER))
        .call();
  }

  public boolean cancelCoinsphOrder(CancelOrderParams params)
      throws IOException, CoinsphException {
    String symbol = null;
    Long orderId = null;
    String clientOrderId = null;

    if (params instanceof CancelOrderByIdParams) {
      orderId = Long.valueOf(((CancelOrderByIdParams) params).getOrderId());
    } else {
      // Coins.ph requires symbol for cancellation
      throw new IllegalArgumentException(
          "CancelOrderParams must implement CancelOrderByIdParams and CancelOrderByCurrencyPair for Coins.ph");
    }
    
    if (params instanceof CancelOrderByCurrencyPair) {
        symbol = CoinsphAdapters.toSymbol(((CancelOrderByCurrencyPair) params).getCurrencyPair());
    } else {
        throw new IllegalArgumentException(
          "CancelOrderParams must implement CancelOrderByCurrencyPair for Coins.ph");
    }
final String finalSymbol = symbol;
    final Long finalOrderId = orderId;


    // TODO: clientOrderId cancellation if API supports it
    // if (params instanceof CancelOrderByClientOrderIdParams) {
    //   clientOrderId = ((CancelOrderByClientOrderIdParams) params).getClientOrderId();
    // }

    CoinsphOrder cancelledOrder = decorateApiCall(
            () ->
                coinsphAuthenticated.cancelOrder(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    finalSymbol,
                    finalOrderId,
                    clientOrderId, // origClientOrderId
                    exchange.getRecvWindow()))
        // .withRetry(retry("cancelOrder"))
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
    
    // Coins.ph cancel returns the cancelled order. We need to check status.
    return "CANCELED".equalsIgnoreCase(cancelledOrder.getStatus()) || "EXPIRED".equalsIgnoreCase(cancelledOrder.getStatus());
  }

  public CoinsphOrder getCoinsphOrderStatus(String orderId, String symbol)
      throws IOException, CoinsphException {
    return decorateApiCall(
            () ->
                coinsphAuthenticated.getOrderStatus(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    symbol,
                    Long.valueOf(orderId),
                    null, // origClientOrderId
                    exchange.getRecvWindow()))
        // .withRetry(retry("orderStatus"))
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER))
        .call();
  }

  public List<CoinsphUserTrade> getCoinsphUserTrades(
      String symbol,
      Long orderId, // Optional, not a direct filter in Coins.ph API but can be used post-fetch
      Long startTime,
      Long endTime,
      Long fromTradeId,
      Integer limit)
      throws IOException, CoinsphException {

    return decorateApiCall(
            () ->
                coinsphAuthenticated.getMyTrades(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    symbol,
                    orderId, // Pass along if provided, though API might ignore or use differently
                    startTime,
                    endTime,
                    fromTradeId,
                    limit,
                    exchange.getRecvWindow()))
        // .withRetry(retry("myTrades"))
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER)) // Adjust weight if known, myTrades is 10
        .call();
  }
}