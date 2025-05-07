package org.knowm.xchange.coinsph;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.coinsph.dto.account.CoinsPHAccountInfo;
import org.knowm.xchange.coinsph.dto.account.CoinsPHDepositAddress;
import org.knowm.xchange.coinsph.dto.account.CoinsPHWithdrawResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHCancelOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrderResponse;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHTrade;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHUserDataStream;
import si.mazi.rescu.ParamsDigest;

/** Coins.ph authenticated API endpoints */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface CoinsPHAuthenticated extends CoinsPH {
  String SIGNATURE = "signature";

  /**
   * Get account information
   *
   * @param apiKey the API key
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the account information
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/v1/account")
  CoinsPHAccountInfo getAccountInfo(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Get deposit address
   *
   * @param apiKey the API key
   * @param coin the coin
   * @param network the network (optional)
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the deposit address
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/wallet/v1/deposit/address")
  CoinsPHDepositAddress getDepositAddress(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("coin") String coin,
      @QueryParam("network") String network,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Withdraw funds
   *
   * @param apiKey the API key
   * @param coin the coin
   * @param network the network (optional)
   * @param address the address
   * @param addressTag the address tag (memo/tag for specific coins)
   * @param amount the amount
   * @param withdrawOrderId client order id for withdraw
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the withdraw response
   * @throws IOException if an error occurs
   */
  @POST
  @Path("openapi/wallet/v1/withdraw/apply")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  CoinsPHWithdrawResponse withdraw(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("coin") String coin,
      @QueryParam("network") String network,
      @QueryParam("address") String address,
      @QueryParam("addressTag") String addressTag,
      @QueryParam("amount") String amount,
      @QueryParam("withdrawOrderId") String withdrawOrderId,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Place a new order
   *
   * @param apiKey the API key
   * @param symbol the symbol
   * @param side the side (BUY/SELL)
   * @param type the type (LIMIT/MARKET/etc)
   * @param timeInForce the time in force (GTC/IOC/FOK)
   * @param quantity the quantity
   * @param quoteOrderQty the quote order quantity (for MARKET orders)
   * @param price the price
   * @param newClientOrderId the client order id
   * @param stopPrice the stop price (for STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and
   *     TAKE_PROFIT_LIMIT orders)
   * @param newOrderRespType the response type (ACK, RESULT, FULL)
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the order response
   * @throws IOException if an error occurs
   */
  @POST
  @Path("openapi/v1/order")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  CoinsPHOrderResponse newOrder(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("side") String side,
      @QueryParam("type") String type,
      @QueryParam("timeInForce") String timeInForce,
      @QueryParam("quantity") String quantity,
      @QueryParam("quoteOrderQty") String quoteOrderQty,
      @QueryParam("price") String price,
      @QueryParam("newClientOrderId") String newClientOrderId,
      @QueryParam("stopPrice") String stopPrice,
      @QueryParam("newOrderRespType") String newOrderRespType,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Cancel an order
   *
   * @param apiKey the API key
   * @param symbol the symbol
   * @param orderId the order id
   * @param origClientOrderId the original client order id
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the cancel order response
   * @throws IOException if an error occurs
   */
  @DELETE
  @Path("openapi/v1/order")
  CoinsPHCancelOrderResponse cancelOrder(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Get order status
   *
   * @param apiKey the API key
   * @param symbol the symbol
   * @param orderId the order id
   * @param origClientOrderId the original client order id
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the order
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/v1/order")
  CoinsPHOrder getOrder(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Get open orders
   *
   * @param apiKey the API key
   * @param symbol the symbol (optional)
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the list of open orders
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/v1/openOrders")
  List<CoinsPHOrder> getOpenOrders(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Get order history
   *
   * @param apiKey the API key
   * @param symbol the symbol
   * @param orderId the order id
   * @param startTime the start time
   * @param endTime the end time
   * @param limit the limit
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the list of historical orders
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/v1/historyOrders")
  List<CoinsPHOrder> getHistoryOrders(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Get trade history
   *
   * @param apiKey the API key
   * @param symbol the symbol
   * @param orderId the order id
   * @param startTime the start time
   * @param endTime the end time
   * @param fromId the from id
   * @param limit the limit
   * @param recvWindow the receive window
   * @param timestamp the timestamp
   * @param signature the signature
   * @return the list of trades
   * @throws IOException if an error occurs
   */
  @GET
  @Path("openapi/v1/myTrades")
  List<CoinsPHTrade> getMyTrades(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("fromId") Long fromId,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

  /**
   * Start user data stream
   *
   * @param apiKey the API key
   * @return the user data stream
   * @throws IOException if an error occurs
   */
  @POST
  @Path("openapi/v1/userDataStream")
  CoinsPHUserDataStream startUserDataStream(@HeaderParam("X-COINS-APIKEY") String apiKey)
      throws IOException;

  /**
   * Keep alive user data stream
   *
   * @param apiKey the API key
   * @param listenKey the listen key
   * @return the response
   * @throws IOException if an error occurs
   */
  @jakarta.ws.rs.PUT
  @Path("openapi/v1/userDataStream")
  Map<String, Object> keepAliveUserDataStream(
      @HeaderParam("X-COINS-APIKEY") String apiKey, @QueryParam("listenKey") String listenKey)
      throws IOException;

  /**
   * Close user data stream
   *
   * @param apiKey the API key
   * @param listenKey the listen key
   * @return the response
   * @throws IOException if an error occurs
   */
  @DELETE
  @Path("openapi/v1/userDataStream")
  Map<String, Object> closeUserDataStream(
      @HeaderParam("X-COINS-APIKEY") String apiKey, @QueryParam("listenKey") String listenKey)
      throws IOException;
}
