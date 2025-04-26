package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Account limits information from Coins.ph */
@Getter
@ToString
public class CoinsPHLimits {

  private final BigDecimal cashInLimit;
  private final BigDecimal cashInRemaining;
  private final BigDecimal cashOutLimit;
  private final BigDecimal cashOutRemaining;
  private final BigDecimal totalWithdrawLimit;
  private final BigDecimal totalWithdrawRemaining;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHLimits(
      @JsonProperty("cashInLimit") BigDecimal cashInLimit,
      @JsonProperty("cashInRemaining") BigDecimal cashInRemaining,
      @JsonProperty("cashOutLimit") BigDecimal cashOutLimit,
      @JsonProperty("cashOutRemaining") BigDecimal cashOutRemaining,
      @JsonProperty("totalWithdrawLimit") BigDecimal totalWithdrawLimit,
      @JsonProperty("totalWithdrawRemaining") BigDecimal totalWithdrawRemaining) {
    this.cashInLimit = cashInLimit;
    this.cashInRemaining = cashInRemaining;
    this.cashOutLimit = cashOutLimit;
    this.cashOutRemaining = cashOutRemaining;
    this.totalWithdrawLimit = totalWithdrawLimit;
    this.totalWithdrawRemaining = totalWithdrawRemaining;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
