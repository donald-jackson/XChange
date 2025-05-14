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
    CoinsphNewOrderRequest request = new CoinsphNewOrderRequest();
    request.setSymbol(CoinsphAdapters.toSymbol(marketOrder.getCurrencyPair()));
    request.setSide(CoinsphAdapters.toSide(marketOrder.getType()));
    request.setType(CoinsphAdapters.toOrderType(marketOrder)); // Should be MARKET

    if (marketOrder.hasFlag(CoinsphAdapters.CoinsphOrderFlags.QUOTE_ORDER_QTY)) {
      request.setQuoteOrderQty(marketOrder.getOriginalAmount()); // If flag is set, originalAmount is quote amount
    } else {
      request.setQuantity(marketOrder.getOriginalAmount());
    }
    
    request.setNewClientOrderId(marketOrder.getUserReference());
    request.setRecvWindow(exchange.getRecvWindow());
    // timeInForce is generally not applicable or allowed for MARKET orders by Coins.ph
    // price and stopPrice are not for basic MARKET orders

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    request))
        // .withRetry(retry("newOrder"))
        // .withRateLimiter(rateLimiter(ORDERS_RATE_LIMITER))
        .call();
  }

  public CoinsphOrder placeCoinsphLimitOrder(LimitOrder limitOrder)
      throws IOException, CoinsphException {
    CoinsphNewOrderRequest request = new CoinsphNewOrderRequest();
    request.setSymbol(CoinsphAdapters.toSymbol(limitOrder.getCurrencyPair()));
    request.setSide(CoinsphAdapters.toSide(limitOrder.getType()));
    request.setType(CoinsphAdapters.toOrderType(limitOrder)); // Should be LIMIT
    String timeInForceValue = "GTC"; // Default for limit orders if not specified
    for (Order.IOrderFlags flag : limitOrder.getOrderFlags()) {
      if (flag instanceof org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) {
        timeInForceValue = ((org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) flag).getValue();
        break;
      }
    }
    request.setTimeInForce(timeInForceValue);
    request.setQuantity(limitOrder.getOriginalAmount());
    request.setPrice(limitOrder.getLimitPrice());
    request.setNewClientOrderId(limitOrder.getUserReference());
    request.setRecvWindow(exchange.getRecvWindow());
    // quoteOrderQty and stopPrice are not for basic LIMIT orders

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    request))
        // .withRetry(retry("newOrder"))
        // .withRateLimiter(rateLimiter(ORDERS_RATE_LIMITER))
        .call();
  }

  public CoinsphOrder placeCoinsphStopOrder(org.knowm.xchange.dto.trade.StopOrder stopOrder)
      throws IOException, CoinsphException {
    CoinsphNewOrderRequest request = new CoinsphNewOrderRequest();
    request.setSymbol(CoinsphAdapters.toSymbol(stopOrder.getCurrencyPair()));
    request.setSide(CoinsphAdapters.toSide(stopOrder.getType()));
    
    // Determine Coins.ph order type (STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT)
    // XChange StopOrder doesn't distinguish between STOP_LOSS and TAKE_PROFIT directly.
    // It's usually inferred by stopPrice vs current market price.
    // Coins.ph requires explicit types.
    // For simplicity, we'll assume STOP_LOSS_LIMIT if limitPrice is present, else STOP_LOSS.
    // User might need to use flags to specify TAKE_PROFIT variants.
    if (stopOrder.getLimitPrice() != null) {
        request.setType("STOP_LOSS_LIMIT"); // Or TAKE_PROFIT_LIMIT based on flags/convention
        request.setPrice(stopOrder.getLimitPrice()); // This is the limit price for the triggered order
        String stopOrderTimeInForceValue = "GTC"; // Default for the limit part of a stop-limit order
        for (Order.IOrderFlags flag : stopOrder.getOrderFlags()) {
          if (flag instanceof org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) {
            stopOrderTimeInForceValue = ((org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce) flag).getValue();
            break;
          }
        }
        request.setTimeInForce(stopOrderTimeInForceValue); // e.g. GTC for the limit part
    } else {
        request.setType("STOP_LOSS"); // Or TAKE_PROFIT based on flags/convention
        // For MARKET stop orders (STOP_LOSS, TAKE_PROFIT), timeInForce is usually not applicable/allowed.
    }
    // TODO: Add logic to differentiate STOP_LOSS vs TAKE_PROFIT based on XChange flags or conventions if possible.
    // For now, defaulting to STOP_LOSS variants.

    if (stopOrder.hasFlag(CoinsphAdapters.CoinsphOrderFlags.QUOTE_ORDER_QTY) && stopOrder.getLimitPrice() == null) {
      // Only for STOP_LOSS (market) or TAKE_PROFIT (market) if quoteOrderQty is intended
      request.setQuoteOrderQty(stopOrder.getOriginalAmount());
    } else {
      request.setQuantity(stopOrder.getOriginalAmount());
    }
    request.setStopPrice(stopOrder.getStopPrice());
    request.setNewClientOrderId(stopOrder.getUserReference());
    request.setRecvWindow(exchange.getRecvWindow());

    return decorateApiCall(
            () ->
                coinsphAuthenticated.newOrder(
                    apiKey,
                    timestampFactory,
                    signatureCreator,
                    request))
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