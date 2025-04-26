package org.knowm.xchange.coinsph.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.coinsph.CoinsPH;
import org.knowm.xchange.coinsph.CoinsPHExchange;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;

/** Tests for the CoinsPHMarketDataService class */
public class CoinsPHMarketDataServiceTest {

  private CoinsPHMarketDataService marketDataService;
  private CoinsPH coinsPH;

  @Before
  public void setUp() {
    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(CoinsPHExchange.class);
    coinsPH = mock(CoinsPH.class);

    marketDataService = new CoinsPHMarketDataService(exchange);
    // Use reflection to set the mocked coinsPH field
    try {
      java.lang.reflect.Field field = CoinsPHBaseService.class.getDeclaredField("coinsPH");
      field.setAccessible(true);
      field.set(marketDataService, coinsPH);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testGetTicker() throws IOException {
    // Create test data based on real API response
    CoinsPHTicker24h coinsPHTicker =
        new CoinsPHTicker24h(
            "BTCPHP",
            new BigDecimal("61845.5"),
            new BigDecimal("0.0117"),
            new BigDecimal("5344867.5"),
            new BigDecimal("5285073.6"),
            new BigDecimal("5344867.5"),
            new BigDecimal("0.0001521"),
            new BigDecimal("5343373.2"),
            new BigDecimal("0.000014"),
            new BigDecimal("5349500"),
            new BigDecimal("0.0840428"),
            new BigDecimal("5283022"),
            new BigDecimal("5415999.9"),
            new BigDecimal("5250000.2"),
            new BigDecimal("5.7010804"),
            new BigDecimal("30446945.54"),
            1745531640000L,
            1745618099202L,
            1936054397054968300L,
            1936779424427631000L,
            4053L);

    // Mock the API call
    when(coinsPH.get24hrTicker("BTCPHP")).thenReturn(coinsPHTicker);

    // Call the method under test
    Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(ticker).isNotNull();
    assertThat(ticker.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);
    assertThat(ticker.getLast()).isEqualTo(new BigDecimal("5344867.5"));
    assertThat(ticker.getBid()).isEqualTo(new BigDecimal("5343373.2"));
    assertThat(ticker.getAsk()).isEqualTo(new BigDecimal("5349500"));
    assertThat(ticker.getHigh()).isEqualTo(new BigDecimal("5415999.9"));
    assertThat(ticker.getLow()).isEqualTo(new BigDecimal("5250000.2"));
    assertThat(ticker.getVolume()).isEqualTo(new BigDecimal("5.7010804"));
  }

  @Test
  public void testGetOrderBook() throws IOException {
    // Create test data based on real API response
    List<List<BigDecimal>> bids = new ArrayList<>();
    bids.add(
        Arrays.asList(
            new BigDecimal("5340983.200000000000000000"), new BigDecimal("0.000014000000000000")));
    bids.add(
        Arrays.asList(
            new BigDecimal("5340000.000000000000000000"), new BigDecimal("0.000717300000000000")));
    bids.add(
        Arrays.asList(
            new BigDecimal("5338449.300000000000000000"), new BigDecimal("0.000014000000000000")));
    bids.add(
        Arrays.asList(
            new BigDecimal("5335436.900000000000000000"), new BigDecimal("0.000014000000000000")));
    bids.add(
        Arrays.asList(
            new BigDecimal("5334847.500000000000000000"), new BigDecimal("0.000236300000000000")));

    List<List<BigDecimal>> asks = new ArrayList<>();
    asks.add(
        Arrays.asList(
            new BigDecimal("5349500.000000000000000000"), new BigDecimal("0.084042800000000000")));
    asks.add(
        Arrays.asList(
            new BigDecimal("5359278.300000000000000000"), new BigDecimal("0.022878000000000000")));
    asks.add(
        Arrays.asList(
            new BigDecimal("5359278.400000000000000000"), new BigDecimal("1.004732600000000000")));
    asks.add(
        Arrays.asList(
            new BigDecimal("5359279.600000000000000000"), new BigDecimal("0.001064300000000000")));
    asks.add(
        Arrays.asList(
            new BigDecimal("5362985.800000000000000000"), new BigDecimal("0.042282400000000000")));

    CoinsPHOrderbook coinsPHOrderbook = new CoinsPHOrderbook(83679851896L, bids, asks);

    // Mock the API call - use null for limit to match the default behavior
    when(coinsPH.getOrderBook("BTCPHP", null)).thenReturn(coinsPHOrderbook);

    // Call the method under test
    OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(orderBook).isNotNull();
    assertThat(orderBook.getBids()).hasSize(5);
    assertThat(orderBook.getAsks()).hasSize(5);
    assertThat(orderBook.getBids().get(0).getLimitPrice())
        .isEqualTo(new BigDecimal("5340983.200000000000000000"));
    assertThat(orderBook.getAsks().get(0).getLimitPrice())
        .isEqualTo(new BigDecimal("5349500.000000000000000000"));
  }

  @Test
  public void testGetTrades() throws IOException {
    // Create test data based on real API response
    List<CoinsPHTrade> coinsPHTrades = new ArrayList<>();
    coinsPHTrades.add(
        new CoinsPHTrade(
            1936778921178260000L,
            new BigDecimal("5346033.900000000000000000"),
            new BigDecimal("0.000040100000000000"),
            new BigDecimal("214.37595939"),
            1745618039210L,
            false,
            true));
    coinsPHTrades.add(
        new CoinsPHTrade(
            1936779172786168300L,
            new BigDecimal("5346730.400000000000000000"),
            new BigDecimal("0.000133100000000000"),
            new BigDecimal("711.64981624"),
            1745618069204L,
            true,
            true));
    coinsPHTrades.add(
        new CoinsPHTrade(
            1936779424427631000L,
            new BigDecimal("5344867.500000000000000000"),
            new BigDecimal("0.000152100000000000"),
            new BigDecimal("812.95434675"),
            1745618099202L,
            false,
            true));
    coinsPHTrades.add(
        new CoinsPHTrade(
            1936779927760888300L,
            new BigDecimal("5344949.300000000000000000"),
            new BigDecimal("0.000054900000000000"),
            new BigDecimal("293.43771657"),
            1745618159204L,
            false,
            true));
    coinsPHTrades.add(
        new CoinsPHTrade(
            1936780179419128300L,
            new BigDecimal("5343522.500000000000000000"),
            new BigDecimal("0.000084500000000000"),
            new BigDecimal("451.52765125"),
            1745618189204L,
            true,
            true));

    // Mock the API call - use null for limit to match the default behavior
    when(coinsPH.getTrades("BTCPHP", null)).thenReturn(coinsPHTrades);

    // Call the method under test
    Trades trades = marketDataService.getTrades(CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(trades).isNotNull();
    assertThat(trades.getTrades()).hasSize(5);
    assertThat(trades.getTrades().get(0).getPrice())
        .isEqualTo(new BigDecimal("5346033.900000000000000000"));
    assertThat(trades.getTrades().get(1).getPrice())
        .isEqualTo(new BigDecimal("5346730.400000000000000000"));
    assertThat(trades.getTrades().get(2).getPrice())
        .isEqualTo(new BigDecimal("5344867.500000000000000000"));
    assertThat(trades.getTrades().get(3).getPrice())
        .isEqualTo(new BigDecimal("5344949.300000000000000000"));
    assertThat(trades.getTrades().get(4).getPrice())
        .isEqualTo(new BigDecimal("5343522.500000000000000000"));
  }
}
