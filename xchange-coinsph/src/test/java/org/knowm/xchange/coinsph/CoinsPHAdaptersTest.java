package org.knowm.xchange.coinsph;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;

/** Tests for the CoinsPHAdapters class */
public class CoinsPHAdaptersTest {

  @Test
  public void testAdaptTicker() {
    // Create test data
    CoinsPHTicker24h coinsPHTicker =
        new CoinsPHTicker24h(
            "BTCPHP",
            new BigDecimal("1000"),
            new BigDecimal("2"),
            new BigDecimal("50000"),
            new BigDecimal("49000"),
            new BigDecimal("50000"),
            new BigDecimal("0.1"),
            new BigDecimal("49900"),
            new BigDecimal("1"),
            new BigDecimal("50100"),
            new BigDecimal("1"),
            new BigDecimal("49000"),
            new BigDecimal("51000"),
            new BigDecimal("48000"),
            new BigDecimal("100"),
            new BigDecimal("5000000"),
            1617235200000L,
            1617321600000L,
            12345L,
            12346L,
            100L);

    // Call the method under test
    Ticker ticker = CoinsPHAdapters.adaptTicker(coinsPHTicker, CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(ticker).isNotNull();
    assertThat(ticker.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);
    assertThat(ticker.getLast()).isEqualTo(new BigDecimal("50000"));
    assertThat(ticker.getBid()).isEqualTo(new BigDecimal("49900"));
    assertThat(ticker.getAsk()).isEqualTo(new BigDecimal("50100"));
    assertThat(ticker.getHigh()).isEqualTo(new BigDecimal("51000"));
    assertThat(ticker.getLow()).isEqualTo(new BigDecimal("48000"));
    assertThat(ticker.getVolume()).isEqualTo(new BigDecimal("100"));
    assertThat(ticker.getQuoteVolume()).isEqualTo(new BigDecimal("5000000"));
    assertThat(ticker.getTimestamp()).isEqualTo(new Date(1617321600000L));
  }

  @Test
  public void testAdaptOrderBook() {
    // Create test data
    List<List<BigDecimal>> bids = new ArrayList<>();
    bids.add(Arrays.asList(new BigDecimal("49900"), new BigDecimal("1")));
    bids.add(Arrays.asList(new BigDecimal("49800"), new BigDecimal("2")));

    List<List<BigDecimal>> asks = new ArrayList<>();
    asks.add(Arrays.asList(new BigDecimal("50100"), new BigDecimal("1")));
    asks.add(Arrays.asList(new BigDecimal("50200"), new BigDecimal("2")));

    CoinsPHOrderbook coinsPHOrderbook = new CoinsPHOrderbook(1617321600000L, bids, asks);

    // Call the method under test
    OrderBook orderBook = CoinsPHAdapters.adaptOrderBook(coinsPHOrderbook, CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(orderBook).isNotNull();
    assertThat(orderBook.getBids()).hasSize(2);
    assertThat(orderBook.getAsks()).hasSize(2);

    LimitOrder bid1 = orderBook.getBids().get(0);
    assertThat(bid1.getLimitPrice()).isEqualTo(new BigDecimal("49900"));
    assertThat(bid1.getOriginalAmount()).isEqualTo(new BigDecimal("1"));
    assertThat(bid1.getType()).isEqualTo(OrderType.BID);
    assertThat(bid1.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);

    LimitOrder ask1 = orderBook.getAsks().get(0);
    assertThat(ask1.getLimitPrice()).isEqualTo(new BigDecimal("50100"));
    assertThat(ask1.getOriginalAmount()).isEqualTo(new BigDecimal("1"));
    assertThat(ask1.getType()).isEqualTo(OrderType.ASK);
    assertThat(ask1.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);
  }

  @Test
  public void testAdaptTrades() {
    // Create test data
    List<CoinsPHTrade> coinsPHTrades = new ArrayList<>();
    coinsPHTrades.add(
        new CoinsPHTrade(
            12345L,
            new BigDecimal("50000"),
            new BigDecimal("0.1"),
            new BigDecimal("5000"),
            1617321600000L,
            false,
            true));
    coinsPHTrades.add(
        new CoinsPHTrade(
            12346L,
            new BigDecimal("50100"),
            new BigDecimal("0.2"),
            new BigDecimal("10020"),
            1617321700000L,
            true,
            true));

    // Call the method under test
    Trades trades = CoinsPHAdapters.adaptTrades(coinsPHTrades, CurrencyPair.BTC_PHP);

    // Verify the result
    assertThat(trades).isNotNull();
    assertThat(trades.getTrades()).hasSize(2);

    Trade trade1 = trades.getTrades().get(0);
    assertThat(trade1.getPrice()).isEqualTo(new BigDecimal("50000"));
    assertThat(trade1.getOriginalAmount()).isEqualTo(new BigDecimal("0.1"));
    assertThat(trade1.getType()).isEqualTo(OrderType.ASK);
    assertThat(trade1.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);
    assertThat(trade1.getTimestamp()).isEqualTo(new Date(1617321600000L));
    assertThat(trade1.getId()).isEqualTo("12345");

    Trade trade2 = trades.getTrades().get(1);
    assertThat(trade2.getPrice()).isEqualTo(new BigDecimal("50100"));
    assertThat(trade2.getOriginalAmount()).isEqualTo(new BigDecimal("0.2"));
    assertThat(trade2.getType()).isEqualTo(OrderType.BID);
    assertThat(trade2.getCurrencyPair()).isEqualTo(CurrencyPair.BTC_PHP);
    assertThat(trade2.getTimestamp()).isEqualTo(new Date(1617321700000L));
    assertThat(trade2.getId()).isEqualTo("12346");
  }
}
