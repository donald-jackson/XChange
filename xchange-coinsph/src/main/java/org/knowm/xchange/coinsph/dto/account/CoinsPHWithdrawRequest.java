package org.knowm.xchange.coinsph.dto.account;

import java.math.BigDecimal;

/**
 * Withdraw request for Coins.ph
 */
public class CoinsPHWithdrawRequest {

  private final String coin;
  private final String network;
  private final String address;
  private final String addressTag;
  private final BigDecimal amount;
  private final String withdrawOrderId;

  public CoinsPHWithdrawRequest(
      String coin,
      String network,
      String address,
      String addressTag,
      BigDecimal amount,
      String withdrawOrderId) {
    this.coin = coin;
    this.network = network;
    this.address = address;
    this.addressTag = addressTag;
    this.amount = amount;
    this.withdrawOrderId = withdrawOrderId;
  }

  public String getCoin() {
    return coin;
  }

  public String getNetwork() {
    return network;
  }

  public String getAddress() {
    return address;
  }

  public String getAddressTag() {
    return addressTag;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getWithdrawOrderId() {
    return withdrawOrderId;
  }
}