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
      @JsonProperty("cashInLimit") String cashInLimit,
      @JsonProperty("cashInRemaining") String cashInRemaining,
      @JsonProperty("cashOutLimit") String cashOutLimit,
      @JsonProperty("cashOutRemaining") String cashOutRemaining,
      @JsonProperty("totalWithdrawLimit") String totalWithdrawLimit,
      @JsonProperty("totalWithdrawRemaining") String totalWithdrawRemaining) {
    this.cashInLimit = new BigDecimal(cashInLimit);
    this.cashInRemaining = new BigDecimal(cashInRemaining);
    this.cashOutLimit = new BigDecimal(cashOutLimit);
    this.cashOutRemaining = new BigDecimal(cashOutRemaining);
    this.totalWithdrawLimit = new BigDecimal(totalWithdrawLimit);
    this.totalWithdrawRemaining = new BigDecimal(totalWithdrawRemaining);
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
