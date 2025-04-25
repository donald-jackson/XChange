package org.knowm.xchange.coinsph.dto.marketdata;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * 24hr ticker price change statistics for a symbol
 */
@Getter
@ToString(includeFieldNames = true, of = {"symbol", "lastPrice", "bidPrice", "askPrice", "volume"})
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
  private final Map<String, Object> additionalProperties = new HashMap<>();

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
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}