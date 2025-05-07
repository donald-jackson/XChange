package org.knowm.xchange.coinsph;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.knowm.xchange.coinsph.dto.account.CoinsPHAccountInfo;
import org.knowm.xchange.coinsph.dto.account.CoinsPHDepositAddress;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.coinsph.dto.trade.CoinsPHOrder;
import org.knowm.xchange.coinsph.service.CoinsPHSignatureCreator;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestProxyFactory;

/** Simple test for Coins.ph Sandbox API */
public class CoinsPHSandboxApiTest {

  private static final String API_KEY =
      "MjJGM9XKjXdM073QdG3kMH8ijLjNinfXJlOz4l8JF2QBZWUST3uSvSC9psjIMmZx";
  private static final String API_SECRET =
      "lZBfiG4xgXDYLYpwu0IXvLUmekJvkoLM69q7oxM2ttiup1CPcM8NA9Tr46eKlVnG";
  private static final String BASE_URL = "https://9001.pl-qa.coinsxyz.me";
  private static final String TEST_RESPONSES_DIR = "test-responses";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    // Create directory for test responses
    Path dir = Paths.get(TEST_RESPONSES_DIR);
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }

    // Create API client
    ClientConfig config = new ClientConfig();
    CoinsPH coinsPH = RestProxyFactory.createProxy(CoinsPH.class, BASE_URL, config);

    // Test public endpoints
    testServerTime(coinsPH);
    testExchangeInfo(coinsPH);
    testTicker(coinsPH);
    testOrderbook(coinsPH);
    testTrades(coinsPH);

    // Create authenticated API client
    ParamsDigest signatureCreator = CoinsPHSignatureCreator.createInstance(API_SECRET);
    CoinsPHAuthenticated coinsPHAuthenticated =
        RestProxyFactory.createProxy(CoinsPHAuthenticated.class, BASE_URL, config);

    // Test authenticated endpoints
    testAccountInfo(coinsPHAuthenticated, signatureCreator);
    testDepositAddress(coinsPHAuthenticated, signatureCreator);
    testOpenOrders(coinsPHAuthenticated, signatureCreator);
    testOrderHistory(coinsPHAuthenticated, signatureCreator);
    testTradeHistory(coinsPHAuthenticated, signatureCreator);
  }

  private static void testServerTime(CoinsPH coinsPH) throws IOException {
    System.out.println("Testing server time...");
    CoinsPH.CoinsPHServerTime serverTime = coinsPH.getServerTime();
    saveResponse("server_time.json", serverTime);
    System.out.println("Server time: " + serverTime.getServerTime());
  }

  private static void testExchangeInfo(CoinsPH coinsPH) throws IOException {
    System.out.println("Testing exchange info...");
    CoinsPH.CoinsPHExchangeInfo exchangeInfo = coinsPH.getExchangeInfo();
    saveResponse("exchange_info.json", exchangeInfo);
    System.out.println("Exchange info: " + exchangeInfo.getTimezone());
    System.out.println("Number of symbols: " + exchangeInfo.getSymbols().size());
  }

  private static void testTicker(CoinsPH coinsPH) throws IOException {
    System.out.println("Testing ticker...");
    CoinsPHTicker24h ticker = coinsPH.get24hrTicker("BTCUSDT");
    saveResponse("ticker_BTCUSDT.json", ticker);
    System.out.println("BTCUSDT ticker: " + ticker.getLastPrice());
  }

  private static void testOrderbook(CoinsPH coinsPH) throws IOException {
    System.out.println("Testing orderbook...");
    CoinsPHOrderbook orderbook = coinsPH.getOrderBook("BTCUSDT", 10);
    saveResponse("orderbook_BTCUSDT.json", orderbook);
    System.out.println("BTCUSDT orderbook bids: " + orderbook.getBids().size());
    System.out.println("BTCUSDT orderbook asks: " + orderbook.getAsks().size());
  }

  private static void testTrades(CoinsPH coinsPH) throws IOException {
    System.out.println("Testing trades...");
    List<CoinsPHTrade> trades = coinsPH.getTrades("BTCUSDT", 10);
    saveResponse("trades_BTCUSDT.json", trades);
    System.out.println("BTCUSDT trades: " + trades.size());
  }

  private static void testAccountInfo(
      CoinsPHAuthenticated coinsPHAuthenticated, ParamsDigest signatureCreator) throws IOException {
    System.out.println("Testing account info...");
    try {
      long timestamp = System.currentTimeMillis();
      CoinsPHAccountInfo accountInfo =
          coinsPHAuthenticated.getAccountInfo(API_KEY, 5000L, timestamp, signatureCreator);
      saveResponse("account_info.json", accountInfo);
      System.out.println("Account info: " + accountInfo.getBalances().size() + " balances");
    } catch (Exception e) {
      System.out.println("Error testing account info: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void testDepositAddress(
      CoinsPHAuthenticated coinsPHAuthenticated, ParamsDigest signatureCreator) throws IOException {
    System.out.println("Testing deposit address...");
    try {
      long timestamp = System.currentTimeMillis();
      CoinsPHDepositAddress depositAddress =
          coinsPHAuthenticated.getDepositAddress(
              API_KEY, "BTC", null, 5000L, timestamp, signatureCreator);
      saveResponse("deposit_address_BTC.json", depositAddress);
      System.out.println("BTC deposit address: " + depositAddress.getAddress());
    } catch (Exception e) {
      System.out.println("Error testing deposit address: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void testOpenOrders(
      CoinsPHAuthenticated coinsPHAuthenticated, ParamsDigest signatureCreator) throws IOException {
    System.out.println("Testing open orders...");
    try {
      long timestamp = System.currentTimeMillis();
      List<CoinsPHOrder> openOrders =
          coinsPHAuthenticated.getOpenOrders(API_KEY, null, 5000L, timestamp, signatureCreator);
      saveResponse("open_orders.json", openOrders);
      System.out.println("Open orders: " + openOrders.size());
    } catch (Exception e) {
      System.out.println("Error testing open orders: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void testOrderHistory(
      CoinsPHAuthenticated coinsPHAuthenticated, ParamsDigest signatureCreator) throws IOException {
    System.out.println("Testing order history...");
    try {
      long timestamp = System.currentTimeMillis();
      List<CoinsPHOrder> orderHistory =
          coinsPHAuthenticated.getHistoryOrders(
              API_KEY, "BTCUSDT", null, null, null, 10, 5000L, timestamp, signatureCreator);
      saveResponse("order_history.json", orderHistory);
      System.out.println("Order history: " + orderHistory.size());
    } catch (Exception e) {
      System.out.println("Error testing order history: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void testTradeHistory(
      CoinsPHAuthenticated coinsPHAuthenticated, ParamsDigest signatureCreator) throws IOException {
    System.out.println("Testing trade history...");
    try {
      long timestamp = System.currentTimeMillis();
      List<org.knowm.xchange.coinsph.dto.trade.CoinsPHTrade> tradeHistory =
          coinsPHAuthenticated.getMyTrades(
              API_KEY, "BTCUSDT", null, null, null, null, 10, 5000L, timestamp, signatureCreator);
      saveResponse("trade_history.json", tradeHistory);
      System.out.println("Trade history: " + tradeHistory.size());
    } catch (Exception e) {
      System.out.println("Error testing trade history: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void saveResponse(String filename, Object response) throws IOException {
    String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
    try (PrintWriter out = new PrintWriter(TEST_RESPONSES_DIR + "/" + filename)) {
      out.println(json);
    }
  }
}
