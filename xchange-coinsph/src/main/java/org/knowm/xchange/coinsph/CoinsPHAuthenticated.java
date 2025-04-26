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

  @GET
  @Path("openapi/v1/account")
  CoinsPHAccountInfo getAccountInfo(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

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

  @GET
  @Path("openapi/v1/openOrders")
  List<CoinsPHOrder> getOpenOrders(
      @HeaderParam("X-COINS-APIKEY") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("recvWindow") Long recvWindow,
      @QueryParam("timestamp") Long timestamp,
      @QueryParam("signature") ParamsDigest signature)
      throws IOException;

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

  @POST
  @Path("openapi/v1/userDataStream")
  CoinsPHUserDataStream startUserDataStream(@HeaderParam("X-COINS-APIKEY") String apiKey)
      throws IOException;

  @jakarta.ws.rs.PUT
  @Path("openapi/v1/userDataStream")
  Map<String, Object> keepAliveUserDataStream(
      @HeaderParam("X-COINS-APIKEY") String apiKey, @QueryParam("listenKey") String listenKey)
      throws IOException;

  @DELETE
  @Path("openapi/v1/userDataStream")
  Map<String, Object> closeUserDataStream(
      @HeaderParam("X-COINS-APIKEY") String apiKey, @QueryParam("listenKey") String listenKey)
      throws IOException;
}
