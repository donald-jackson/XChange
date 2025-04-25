package org.knowm.xchange.coinsph.dto.account;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account limits information from Coins.ph
 */
public class CoinsPHLimits {

  private final BigDecimal cashInLimit;
  private final BigDecimal cashInRemaining;
  private final BigDecimal cashOutLimit;
  private final BigDecimal cashOutRemaining;
  private final BigDecimal totalWithdrawLimit;
  private final BigDecimal totalWithdrawRemaining;

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

  public BigDecimal getCashInLimit() {
    return cashInLimit;
  }

  public BigDecimal getCashInRemaining() {
    return cashInRemaining;
  }

  public BigDecimal getCashOutLimit() {
    return cashOutLimit;
  }

  public BigDecimal getCashOutRemaining() {
    return cashOutRemaining;
  }

  public BigDecimal getTotalWithdrawLimit() {
    return totalWithdrawLimit;
  }

  public BigDecimal getTotalWithdrawRemaining() {
    return totalWithdrawRemaining;
  }

  @Override
  public String toString() {
    return "CoinsPHLimits{"
        + "cashInLimit="
        + cashInLimit
        + ", cashInRemaining="
        + cashInRemaining
        + ", cashOutLimit="
        + cashOutLimit
        + ", cashOutRemaining="
        + cashOutRemaining
        + ", totalWithdrawLimit="
        + totalWithdrawLimit
        + ", totalWithdrawRemaining="
        + totalWithdrawRemaining
        + '}';
  }
}