package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParamId;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

/**
 * Implementation of the trade service for Coins.ph
 */
public class CoinsPHTradeService extends CoinsPHTradeServiceRaw implements TradeService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHTradeService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public OpenOrders getOpenOrders() throws IOException {
    return getOpenOrders(createOpenOrdersParams());
  }

  @Override
  public OpenOrders getOpenOrders(OpenOrdersParams params) throws IOException {
    CurrencyPair currencyPair = null;
    if (params instanceof DefaultOpenOrdersParamCurrencyPair) {
      currencyPair = ((DefaultOpenOrdersParamCurrencyPair) params).getCurrencyPair();
    }
    
    List<CoinsPHOrder> coinsPHOrders = getCoinsPHOpenOrders(currencyPair);
    List<LimitOrder> limitOrders = new ArrayList<>();
    
    for (CoinsPHOrder order : coinsPHOrders) {
      limitOrders.add(adaptLimitOrder(order));
    }
    
    return new OpenOrders(limitOrders);
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
    return placeCoinsPHMarketOrder(marketOrder).getOrderId() + "";
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    return placeCoinsPHLimitOrder(limitOrder).getOrderId() + "";
  }

  @Override
  public String placeStopOrder(StopOrder stopOrder) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean cancelOrder(String orderId) throws IOException {
    return cancelOrder(new DefaultCancelOrderParamId(orderId));
  }

  @Override
  public boolean cancelOrder(CancelOrderParams orderParams) throws IOException {
    if (orderParams instanceof DefaultCancelOrderParamId) {
      String orderId = ((DefaultCancelOrderParamId) orderParams).getOrderId();
      // Note: This is a simplification. In a real implementation, you would need to know the currency pair
      // for the order. This might require maintaining a map of order IDs to currency pairs or fetching
      // open orders first to find the matching order.
      cancelCoinsPHOrder(null, Long.parseLong(orderId));
      return true;
    }
    return false;
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public OpenOrdersParams createOpenOrdersParams() {
    return new DefaultOpenOrdersParamCurrencyPair();
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Adapt a CoinsPHOrder to a LimitOrder
   *
   * @param order the CoinsPHOrder
   * @return the LimitOrder
   */
  private LimitOrder adaptLimitOrder(CoinsPHOrder order) {
    Order.OrderType orderType = order.getSide().equals("BUY") ? Order.OrderType.BID : Order.OrderType.ASK;
    CurrencyPair currencyPair = new CurrencyPair(
        order.getSymbol().substring(0, 3), order.getSymbol().substring(3));
    
    Order.OrderStatus status = Order.OrderStatus.UNKNOWN;
    switch (order.getStatus()) {
      case "NEW":
        status = Order.OrderStatus.NEW;
        break;
      case "PARTIALLY_FILLED":
        status = Order.OrderStatus.PARTIALLY_FILLED;
        break;
      case "FILLED":
        status = Order.OrderStatus.FILLED;
        break;
      case "CANCELED":
        status = Order.OrderStatus.CANCELED;
        break;
      case "REJECTED":
        status = Order.OrderStatus.REJECTED;
        break;
      case "EXPIRED":
        status = Order.OrderStatus.EXPIRED;
        break;
    }
    
    LimitOrder limitOrder = new LimitOrder.Builder(orderType, currencyPair)
        .id(String.valueOf(order.getOrderId()))
        .originalAmount(order.getOrigQty())
        .limitPrice(order.getPrice())
        .timestamp(new java.util.Date(order.getTime()))
        .cumulativeAmount(order.getExecutedQty())
        .userReference(order.getClientOrderId())
        .build();
    limitOrder.setOrderStatus(status);
    return limitOrder;
  }
}