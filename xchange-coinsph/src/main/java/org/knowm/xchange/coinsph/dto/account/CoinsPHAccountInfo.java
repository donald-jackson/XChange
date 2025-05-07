package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Account information from Coins.ph */
@Getter
@ToString
public class CoinsPHAccountInfo {

  private final String accountType;
  private final boolean canDeposit;
  private final boolean canTrade;
  private final boolean canWithdraw;
  private final List<CoinsPHBalance> balances;
  private final String token;
  private final CoinsPHLimits daily;
  private final CoinsPHLimits monthly;
  private final CoinsPHLimits annually;
  private final long updateTime;
  private final Integer code;
  private final String msg;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHAccountInfo(
      @JsonProperty("accountType") String accountType,
      @JsonProperty("canDeposit") boolean canDeposit,
      @JsonProperty("canTrade") boolean canTrade,
      @JsonProperty("canWithdraw") boolean canWithdraw,
      @JsonProperty("balances") List<CoinsPHBalance> balances,
      @JsonProperty("token") String token,
      @JsonProperty("daily") CoinsPHLimits daily,
      @JsonProperty("monthly") CoinsPHLimits monthly,
      @JsonProperty("annually") CoinsPHLimits annually,
      @JsonProperty("updateTime") long updateTime,
      @JsonProperty("code") Integer code,
      @JsonProperty("msg") String msg) {
    this.accountType = accountType;
    this.canDeposit = canDeposit;
    this.canTrade = canTrade;
    this.canWithdraw = canWithdraw;
    this.balances = balances;
    this.token = token;
    this.daily = daily;
    this.monthly = monthly;
    this.annually = annually;
    this.updateTime = updateTime;
    this.code = code;
    this.msg = msg;
  }

  /**
   * Check if the response is an error
   *
   * @return true if the response is an error
   */
  public boolean isError() {
    return code != null && code < 0;
  }

  /**
   * Get the error message if this is an error response
   *
   * @return the error message or null if not an error
   */
  public String getErrorMessage() {
    return isError() ? msg : null;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
