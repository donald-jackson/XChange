package org.knowm.xchange.coinsph.dto.account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * Balance information from Coins.ph
 */
@Getter
@ToString
public class CoinsPHBalance {

  private final String asset;
  private final BigDecimal free;
  private final BigDecimal locked;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHBalance(
      @JsonProperty("asset") String asset,
      @JsonProperty("free") BigDecimal free,
      @JsonProperty("locked") BigDecimal locked) {
    this.asset = asset;
    this.free = free;
    this.locked = locked;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}