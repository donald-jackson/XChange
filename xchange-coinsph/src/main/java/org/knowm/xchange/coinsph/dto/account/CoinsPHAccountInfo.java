package org.knowm.xchange.coinsph.dto.account;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account information from Coins.ph
 */
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
      @JsonProperty("updateTime") long updateTime) {
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
  }

  public String getAccountType() {
    return accountType;
  }

  public boolean isCanDeposit() {
    return canDeposit;
  }

  public boolean isCanTrade() {
    return canTrade;
  }

  public boolean isCanWithdraw() {
    return canWithdraw;
  }

  public List<CoinsPHBalance> getBalances() {
    return balances;
  }

  public String getToken() {
    return token;
  }

  public CoinsPHLimits getDaily() {
    return daily;
  }

  public CoinsPHLimits getMonthly() {
    return monthly;
  }

  public CoinsPHLimits getAnnually() {
    return annually;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  @Override
  public String toString() {
    return "CoinsPHAccountInfo{"
        + "accountType='"
        + accountType
        + '\''
        + ", canDeposit="
        + canDeposit
        + ", canTrade="
        + canTrade
        + ", canWithdraw="
        + canWithdraw
        + ", balances="
        + balances
        + ", token='"
        + token
        + '\''
        + ", updateTime="
        + updateTime
        + '}';
  }
}