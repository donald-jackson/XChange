package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHCancelOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHTrade;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHUserDataStream;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;

/** Implementation of the trade service for Coins.ph */
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
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.newOrder(
        apiKey,
        formatSymbol(limitOrder.getInstrument()),
        limitOrder.getType() == OrderType.BID ? "BUY" : "SELL",
        "LIMIT",
        "GTC",
        limitOrder.getOriginalAmount().toPlainString(),
        null,
        limitOrder.getLimitPrice().toPlainString(),
        limitOrder.getUserReference(),
        null,
        null,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Place a market order
   *
   * @param marketOrder the market order
   * @return the order response
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderResponse placeCoinsPHMarketOrder(MarketOrder marketOrder) throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.newOrder(
        apiKey,
        formatSymbol(marketOrder.getInstrument()),
        marketOrder.getType() == OrderType.BID ? "BUY" : "SELL",
        "MARKET",
        null,
        marketOrder.getOriginalAmount().toPlainString(),
        null,
        null,
        marketOrder.getUserReference(),
        null,
        null,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Cancel an order
   *
   * @param instrument the instrument
   * @param orderId the order id
   * @return the cancel order response
   * @throws IOException if an error occurs
   */
  public CoinsPHCancelOrderResponse cancelCoinsPHOrder(Instrument instrument, long orderId)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.cancelOrder(
        apiKey,
        instrument != null ? formatSymbol(instrument) : null,
        orderId,
        null,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get order status
   *
   * @param instrument the instrument
   * @param orderId the order id
   * @return the order
   * @throws IOException if an error occurs
   */
  public CoinsPHOrder getCoinsPHOrder(Instrument instrument, long orderId) throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getOrder(
        apiKey,
        formatSymbol(instrument),
        orderId,
        null,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get order status
   *
   * @param symbol the symbol
   * @param orderId the order id
   * @param origClientOrderId the original client order id
   * @return the order
   * @throws IOException if an error occurs
   */
  public CoinsPHOrder getCoinsPHOrder(String symbol, Long orderId, String origClientOrderId)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getOrder(
        apiKey, symbol, orderId, origClientOrderId, recvWindow, timestamp, coinsPHSignatureCreator);
  }

  /**
   * Get open orders
   *
   * @param instrument the instrument
   * @return the open orders
   * @throws IOException if an error occurs
   */
  public List<CoinsPHOrder> getCoinsPHOpenOrders(Instrument instrument) throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getOpenOrders(
        apiKey,
        instrument != null ? formatSymbol(instrument) : null,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get order history
   *
   * @param instrument the instrument
   * @param limit the limit of orders to return
   * @return the order history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHOrder> getCoinsPHOrderHistory(Instrument instrument, Integer limit)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getHistoryOrders(
        apiKey,
        formatSymbol(instrument),
        null,
        null,
        null,
        limit,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get order history
   *
   * @param symbol the symbol
   * @param orderId the order id
   * @param startTime the start time
   * @param endTime the end time
   * @param limit the limit of orders to return
   * @return the order history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHOrder> getCoinsPHHistoryOrders(
      String symbol, Long orderId, Long startTime, Long endTime, Integer limit) throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getHistoryOrders(
        apiKey,
        symbol,
        orderId,
        startTime,
        endTime,
        limit,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get trade history
   *
   * @param instrument the instrument
   * @param limit the limit of trades to return
   * @return the trade history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHTradeHistory(Instrument instrument, Integer limit)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getMyTrades(
        apiKey,
        formatSymbol(instrument),
        null,
        null,
        null,
        null,
        limit,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get trade history
   *
   * @param symbol the symbol
   * @param orderId the order id
   * @param startTime the start time
   * @param endTime the end time
   * @param fromId the from id
   * @param limit the limit of trades to return
   * @return the trade history
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHMyTrades(
      String symbol, Long orderId, Long startTime, Long endTime, Long fromId, Integer limit)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getMyTrades(
        apiKey,
        symbol,
        orderId,
        startTime,
        endTime,
        fromId,
        limit,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
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
   * Format the instrument to a symbol
   *
   * @param instrument the instrument
   * @return the symbol
   */
  private String formatSymbol(Instrument instrument) {
    return instrument.toString().replace("/", "").replace(" ", "").replace("-", "_");
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
