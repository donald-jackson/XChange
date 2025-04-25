package org.knowm.xchange.coinsph.dto.marketdata;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 24hr ticker price change statistics for a symbol
 */
public class CoinsPHTicker24h {

  private final String symbol;
  private final BigDecimal priceChange;
  private final BigDecimal priceChangePercent;
  private final BigDecimal weightedAvgPrice;
  private final BigDecimal prevClosePrice;
  private final BigDecimal lastPrice;
  private final BigDecimal lastQty;
  private final BigDecimal bidPrice;
  private final BigDecimal bidQty;
  private final BigDecimal askPrice;
  private final BigDecimal askQty;
  private final BigDecimal openPrice;
  private final BigDecimal highPrice;
  private final BigDecimal lowPrice;
  private final BigDecimal volume;
  private final BigDecimal quoteVolume;
  private final long openTime;
  private final long closeTime;
  private final long firstId;
  private final long lastId;
  private final long count;

  public CoinsPHTicker24h(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("priceChange") BigDecimal priceChange,
      @JsonProperty("priceChangePercent") BigDecimal priceChangePercent,
      @JsonProperty("weightedAvgPrice") BigDecimal weightedAvgPrice,
      @JsonProperty("prevClosePrice") BigDecimal prevClosePrice,
      @JsonProperty("lastPrice") BigDecimal lastPrice,
      @JsonProperty("lastQty") BigDecimal lastQty,
      @JsonProperty("bidPrice") BigDecimal bidPrice,
      @JsonProperty("bidQty") BigDecimal bidQty,
      @JsonProperty("askPrice") BigDecimal askPrice,
      @JsonProperty("askQty") BigDecimal askQty,
      @JsonProperty("openPrice") BigDecimal openPrice,
      @JsonProperty("highPrice") BigDecimal highPrice,
      @JsonProperty("lowPrice") BigDecimal lowPrice,
      @JsonProperty("volume") BigDecimal volume,
      @JsonProperty("quoteVolume") BigDecimal quoteVolume,
      @JsonProperty("openTime") long openTime,
      @JsonProperty("closeTime") long closeTime,
      @JsonProperty("firstId") long firstId,
      @JsonProperty("lastId") long lastId,
      @JsonProperty("count") long count) {
    this.symbol = symbol;
    this.priceChange = priceChange;
    this.priceChangePercent = priceChangePercent;
    this.weightedAvgPrice = weightedAvgPrice;
    this.prevClosePrice = prevClosePrice;
    this.lastPrice = lastPrice;
    this.lastQty = lastQty;
    this.bidPrice = bidPrice;
    this.bidQty = bidQty;
    this.askPrice = askPrice;
    this.askQty = askQty;
    this.openPrice = openPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.volume = volume;
    this.quoteVolume = quoteVolume;
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.firstId = firstId;
    this.lastId = lastId;
    this.count = count;
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getPriceChange() {
    return priceChange;
  }

  public BigDecimal getPriceChangePercent() {
    return priceChangePercent;
  }

  public BigDecimal getWeightedAvgPrice() {
    return weightedAvgPrice;
  }

  public BigDecimal getPrevClosePrice() {
    return prevClosePrice;
  }

  public BigDecimal getLastPrice() {
    return lastPrice;
  }

  public BigDecimal getLastQty() {
    return lastQty;
  }

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public BigDecimal getBidQty() {
    return bidQty;
  }

  public BigDecimal getAskPrice() {
    return askPrice;
  }

  public BigDecimal getAskQty() {
    return askQty;
  }

  public BigDecimal getOpenPrice() {
    return openPrice;
  }

  public BigDecimal getHighPrice() {
    return highPrice;
  }

  public BigDecimal getLowPrice() {
    return lowPrice;
  }

  public BigDecimal getVolume() {
    return volume;
  }

  public BigDecimal getQuoteVolume() {
    return quoteVolume;
  }

  public long getOpenTime() {
    return openTime;
  }

  public long getCloseTime() {
    return closeTime;
  }

  public long getFirstId() {
    return firstId;
  }

  public long getLastId() {
    return lastId;
  }

  public long getCount() {
    return count;
  }

  @Override
  public String toString() {
    return "CoinsPHTicker24h{"
        + "symbol='"
        + symbol
        + '\''
        + ", lastPrice="
        + lastPrice
        + ", bidPrice="
        + bidPrice
        + ", askPrice="
        + askPrice
        + ", volume="
        + volume
        + '}';
  }
}