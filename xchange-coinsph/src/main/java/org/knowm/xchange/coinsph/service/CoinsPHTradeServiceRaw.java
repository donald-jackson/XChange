package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHCancelOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHNewOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHTrade;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHUserDataStream;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;

/**
 * Implementation of the trade service for Coins.ph
 */
public class CoinsPHTradeServiceRaw extends CoinsPHBaseService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  /**
   * Place a limit order
   *
   * @param limitOrder the limit order
   * @return the order response
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderResponse placeCoinsPHLimitOrder(LimitOrder limitOrder) throws IOException {
    return coinsPHAuthenticated.newOrder(
        apiKey,
        formatSymbol(limitOrder.getCurrencyPair()),
        limitOrder.getType() == OrderType.BID ? "BUY" : "SELL",
        "LIMIT",
        "GTC",
        limitOrder.getOriginalAmount().toPlainString(),
        null,
        limitOrder.getLimitPrice().toPlainString(),
        limitOrder.getUserReference(),
        null,
        null,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Place a market order
   *
   * @param marketOrder the market order
   * @return the order response
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderResponse placeCoinsPHMarketOrder(MarketOrder marketOrder) throws IOException {
    return coinsPHAuthenticated.newOrder(
        apiKey,
        formatSymbol(marketOrder.getCurrencyPair()),
        marketOrder.getType() == OrderType.BID ? "BUY" : "SELL",
        "MARKET",
        null,
        marketOrder.getOriginalAmount().toPlainString(),
        null,
        null,
        marketOrder.getUserReference(),
        null,
        null,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Cancel an order
   *
   * @param currencyPair the currency pair
   * @param orderId the order id
   * @return the cancel order response
   * @throws IOException if an error occurs
   */
  public CoinsPHCancelOrderResponse cancelCoinsPHOrder(CurrencyPair currencyPair, long orderId) throws IOException {
    return coinsPHAuthenticated.cancelOrder(
        apiKey,
        formatSymbol(currencyPair),
        orderId,
        null,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Get order status
   *
   * @param currencyPair the currency pair
   * @param orderId the order id
   * @return the order
   * @throws IOException if an error occurs
   */
  public CoinsPHOrder getCoinsPHOrder(CurrencyPair currencyPair, long orderId) throws IOException {
    return coinsPHAuthenticated.getOrder(
        apiKey,
        formatSymbol(currencyPair),
        orderId,
        null,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Get open orders
   *
   * @param currencyPair the currency pair
   * @return the open orders
   * @throws IOException if an error occurs
   */
  public List<CoinsPHOrder> getCoinsPHOpenOrders(CurrencyPair currencyPair) throws IOException {
    return coinsPHAuthenticated.getOpenOrders(
        apiKey,
        currencyPair != null ? formatSymbol(currencyPair) : null,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Get order history
   *
   * @param currencyPair the currency pair
   * @param limit the limit of orders to return
   * @return the order history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHOrder> getCoinsPHOrderHistory(CurrencyPair currencyPair, Integer limit) throws IOException {
    return coinsPHAuthenticated.getHistoryOrders(
        apiKey,
        formatSymbol(currencyPair),
        null,
        null,
        null,
        limit,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Get trade history
   *
   * @param currencyPair the currency pair
   * @param limit the limit of trades to return
   * @return the trade history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHTradeHistory(CurrencyPair currencyPair, Integer limit) throws IOException {
    return coinsPHAuthenticated.getMyTrades(
        apiKey,
        formatSymbol(currencyPair),
        null,
        null,
        null,
        null,
        limit,
        getRecvWindow(),
        exchange.getNonceFactory().createValue(),
        signatureCreator);
  }

  /**
   * Start user data stream
   *
   * @return the user data stream
   * @throws IOException if an error occurs
   */
  public CoinsPHUserDataStream startCoinsPHUserDataStream() throws IOException {
    return coinsPHAuthenticated.startUserDataStream(apiKey);
  }

  /**
   * Keep alive user data stream
   *
   * @param listenKey the listen key
   * @return the response
   * @throws IOException if an error occurs
   */
  public Map<String, Object> keepAliveCoinsPHUserDataStream(String listenKey) throws IOException {
    return coinsPHAuthenticated.keepAliveUserDataStream(apiKey, listenKey);
  }

  /**
   * Close user data stream
   *
   * @param listenKey the listen key
   * @return the response
   * @throws IOException if an error occurs
   */
  public Map<String, Object> closeCoinsPHUserDataStream(String listenKey) throws IOException {
    return coinsPHAuthenticated.closeUserDataStream(apiKey, listenKey);
  }

  /**
   * Format currency pair to Coins.ph symbol format
   *
   * @param currencyPair the currency pair
   * @return the formatted symbol
   */
  private String formatSymbol(CurrencyPair currencyPair) {
    return currencyPair.getBase().getCurrencyCode() + currencyPair.getCounter().getCurrencyCode();
  }

  /**
   * Get receive window
   *
   * @return the receive window
   */
  private Long getRecvWindow() {
    return 5000L;
  }
}