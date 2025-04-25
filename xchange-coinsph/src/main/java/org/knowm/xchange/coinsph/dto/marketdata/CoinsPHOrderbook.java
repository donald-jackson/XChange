package org.knowm.xchange.coinsph.dto.marketdata;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * Order book data from Coins.ph
 */
@Getter
@ToString
public class CoinsPHOrderbook {

  private final long lastUpdateId;
  private final List<List<BigDecimal>> bids;
  private final List<List<BigDecimal>> asks;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHOrderbook(
      @JsonProperty("lastUpdateId") long lastUpdateId,
      @JsonProperty("bids") List<List<BigDecimal>> bids,
      @JsonProperty("asks") List<List<BigDecimal>> asks) {
    this.lastUpdateId = lastUpdateId;
    this.bids = bids;
    this.asks = asks;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}