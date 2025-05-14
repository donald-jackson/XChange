package org.knowm.xchange.coinsph;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
// import jakarta.ws.rs.PUT; // Not used yet
import jakarta.ws.rs.Path;
// import jakarta.ws.rs.PathParam; // Not used yet
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
// import java.util.Map; // Not used yet

// TODO: Import Coins.ph specific DTOs as they are created
import org.knowm.xchange.coinsph.dto.CoinsphException;
import org.knowm.xchange.coinsph.dto.account.CoinsphAccount;
import org.knowm.xchange.coinsph.dto.account.CoinsphTradeFee; // New DTO for trade fees
import org.knowm.xchange.coinsph.dto.account.CoinsphListenKey; // DTO for listen key
import org.knowm.xchange.coinsph.dto.trade.CoinsphNewOrderRequest; // New DTO for request
import org.knowm.xchange.coinsph.dto.trade.CoinsphOrder;
import org.knowm.xchange.coinsph.dto.trade.CoinsphUserTrade;
import org.knowm.xchange.coinsph.dto.trade.CoinsphOrderSide; // Enum
import org.knowm.xchange.coinsph.dto.trade.CoinsphOrderType; // Enum
import org.knowm.xchange.coinsph.dto.trade.CoinsphTimeInForce; // Enum

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

@Path("/openapi/v1") // Base path for v1 of Coins.ph API
@Produces(MediaType.APPLICATION_JSON)
public interface CoinsphAuthenticated extends Coinsph {

  String X_COINS_APIKEY = "X-COINS-APIKEY"; // Header name for API key
  String X_COINS_TIMESTAMP = "X-COINS-TIMESTAMP"; // Header name for timestamp
  String X_COINS_SIGNATURE = "X-COINS-SIGNATURE"; // Header name for signature

  // According to docs, signature is passed in header X-COINS-SIGNATURE
  // timestamp is also in header X-COINS-TIMESTAMP
  // API key is in header X-COINS-APIKEY
  // recvWindow is a query parameter

  /**
   * Get current account information.
   *
   * @param recvWindow optional, The value cannot be greater than 60000
   * @param timestamp This will be used by Rescu to generate the X-COINS-TIMESTAMP header.
   * @param apiKey
   * @param signature This will be used by Rescu to generate the X-COINS-SIGNATURE header.
   * @return
   * @throws IOException
   * @throws org.knowm.xchange.coinsph.dto.CoinsphException
   */
  @GET
  @Path("account")
  CoinsphAccount getAccount(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  /**
   * Get asset trade fees.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Optional. Trading symbol (e.g., BTCPHP). If not sent, fees for all symbols are returned.
   * @param recvWindow Optional.
   * @return List of trade fees
   * @throws IOException
   * @throws CoinsphException
   */
  @GET
  @Path("asset/tradeFee")
  List<CoinsphTradeFee> getTradeFee(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  /**
   * Send in a new order.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Trading symbol (e.g., BTCPHP)
   * @param side BUY or SELL
   * @param type LIMIT, MARKET, STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT, LIMIT_MAKER
   * @param timeInForce Optional. GTC, IOC, FOK
   * @param quantity Order quantity
   * @param quoteOrderQty Optional. For MARKET orders, the amount of quote asset to spend/receive
   * @param price Optional. Order price, required for LIMIT orders
   * @param newClientOrderId Optional. A unique id for the order. Automatically generated if not sent.
   * @param stopPrice Optional. Used with STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
   * @param recvWindow Optional. The value cannot be greater than 60000
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @POST
  @Path("order")
  CoinsphOrder newOrder(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      CoinsphNewOrderRequest newOrderRequest // Request body as DTO
      // recvWindow is now part of CoinsphNewOrderRequest if needed by API,
      // or can be added as a @QueryParam if it's a query parameter for this POST
      // For Coins.ph, recvWindow is a query param for signed endpoints, so it should be separate.
      // However, the newOrderRequest DTO already includes it as an optional field.
      // The API docs for POST /order show recvWindow as a query parameter.
      // Let's keep it as a query param for consistency with other signed endpoints.
      // @QueryParam("recvWindow") Long recvWindow // This is usually for GET/DELETE. POST body should contain all.
      // Let's assume recvWindow, if needed for POST, is part of the signed payload, so it's in the DTO.
      // If it's a query param for POST, it needs to be added separately.
      // The docs say: "All parameters should be sent in the JSON body for POST requests."
      // "For GET, DELETE requests, parameters should be sent in the query string."
      // "recvWindow is applicable to all signed endpoints."
      // This implies for POST, recvWindow should be in the JSON body if it's part of the signed content.
      // The DTO CoinsphNewOrderRequest includes recvWindow.
      )
      throws IOException, CoinsphException;

  /**
   * Check an order's status.
   * Either orderId or origClientOrderId must be sent.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Trading symbol
   * @param orderId Optional. Order ID
   * @param origClientOrderId Optional. Client order ID
   * @param recvWindow Optional.
   * @return
   * @throws IOException
   * @throws org.knowm.xchange.coinsph.dto.CoinsphException
   */
  @GET
  @Path("order")
  CoinsphOrder getOrderStatus(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  /**
   * Cancel an active order.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Trading symbol
   * @param orderId Optional. Order ID
   * @param origClientOrderId Optional. Client order ID
   * @param recvWindow Optional.
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @DELETE
  @Path("order")
  CoinsphOrder cancelOrder(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId,
      @QueryParam("origClientOrderId") String origClientOrderId,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  // User Data Stream
  // =================================================================================================

  /**
   * Start a new user data stream.
   * The stream will close after 60 minutes unless a keepalive is sent.
   *
   * @param apiKey API key
   * @param timestamp
   * @param signature
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @POST
  @Path("userDataStream")
  CoinsphListenKey createListenKey(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature)
      throws IOException, CoinsphException;

  /**
   * Keepalive a user data stream to prevent it from closing.
   * User data streams will close after 60 minutes. It's recommended to send a ping about every 30 minutes.
   *
   * @param apiKey API key
   * @param timestamp
   * @param signature
   * @param listenKey Listen key
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @PUT
  @Path("userDataStream")
  Void keepAliveListenKey(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("listenKey") String listenKey)
      throws IOException, CoinsphException;

  /**
   * Close a user data stream.
   *
   * @param apiKey API key
   * @param timestamp
   * @param signature
   * @param listenKey Listen key
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @DELETE
  @Path("userDataStream")
  Void closeListenKey(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("listenKey") String listenKey)
      throws IOException, CoinsphException;

  /**
   * Get all open orders on a symbol or all symbols.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Optional. If not sent, orders for all symbols will be returned.
   * @param recvWindow Optional.
   * @return
   * @throws IOException
   * @throws org.knowm.xchange.coinsph.dto.CoinsphException
   */
  @GET
  @Path("openOrders")
  List<CoinsphOrder> getOpenOrders(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  /**
   * Get all account orders; active, canceled, or filled.
   * This seems to map to /openapi/v1/historyOrders in Coins.ph docs
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Trading symbol
   * @param startTime Optional. Timestamp in ms
   * @param endTime Optional. Timestamp in ms
   * @param limit Optional. Default 500; max 1000.
   * @param recvWindow Optional.
   * @return
   * @throws IOException
   * @throws CoinsphException
   */
  @GET
  @Path("historyOrders") // Changed from allOrders to match Coins.ph docs
  List<CoinsphOrder> getHistoryOrders(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      // @QueryParam("orderId") Long orderId, // Coins.ph uses startTime/endTime for history
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

  /**
   * Get trades for a specific account and symbol.
   *
   * @param apiKey
   * @param timestamp
   * @param signature
   * @param symbol Trading symbol
   * @param orderId Optional. This is not a direct filter in Coins.ph, but can be used to filter results post-fetch if needed.
   * @param startTime Optional. Timestamp in ms
   * @param endTime Optional. Timestamp in ms
   * @param fromTradeId Optional. Trade Id to fetch from. Default gets most recent trades.
   * @param limit Optional. Default 500; max 1000.
   * @param recvWindow Optional.
   * @return
   * @throws IOException
   * @throws org.knowm.xchange.coinsph.dto.CoinsphException
   */
  @GET
  @Path("myTrades")
  List<CoinsphUserTrade> getMyTrades(
      @HeaderParam(X_COINS_APIKEY) String apiKey,
      @HeaderParam(X_COINS_TIMESTAMP) SynchronizedValueFactory<Long> timestamp,
      @HeaderParam(X_COINS_SIGNATURE) ParamsDigest signature,
      @QueryParam("symbol") String symbol,
      @QueryParam("orderId") Long orderId, // Not a direct filter in Coins.ph
      @QueryParam("startTime") Long startTime,
      @QueryParam("endTime") Long endTime,
      @QueryParam("fromTradeId") Long fromTradeId, 
      @QueryParam("limit") Integer limit,
      @QueryParam("recvWindow") Long recvWindow)
      throws IOException, CoinsphException;

}