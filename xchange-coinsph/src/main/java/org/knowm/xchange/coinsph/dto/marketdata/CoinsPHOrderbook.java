package org.knowm.xchange.coinsph.dto.marketdata;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order book data from Coins.ph
 */
public class CoinsPHOrderbook {

  private final long lastUpdateId;
  private final List<List<BigDecimal>> bids;
  private final List<List<BigDecimal>> asks;

  public CoinsPHOrderbook(
      @JsonProperty("lastUpdateId") long lastUpdateId,
      @JsonProperty("bids") List<List<BigDecimal>> bids,
      @JsonProperty("asks") List<List<BigDecimal>> asks) {
    this.lastUpdateId = lastUpdateId;
    this.bids = bids;
    this.asks = asks;
  }

  public long getLastUpdateId() {
    return lastUpdateId;
  }

  public List<List<BigDecimal>> getBids() {
    return bids;
  }

  public List<List<BigDecimal>> getAsks() {
    return asks;
  }

  @Override
  public String toString() {
    return "CoinsPHOrderbook{"
        + "lastUpdateId="
        + lastUpdateId
        + ", bids="
        + bids
        + ", asks="
        + asks
        + '}';
  }
}