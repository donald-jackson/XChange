package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Deposit address information from Coins.ph
 */
public class CoinsPHDepositAddress {

  private final String coin;
  private final String address;
  private final String addressTag;

  public CoinsPHDepositAddress(
      @JsonProperty("coin") String coin,
      @JsonProperty("address") String address,
      @JsonProperty("addressTag") String addressTag) {
    this.coin = coin;
    this.address = address;
    this.addressTag = addressTag;
  }

  public String getCoin() {
    return coin;
  }

  public String getAddress() {
    return address;
  }

  public String getAddressTag() {
    return addressTag;
  }

  @Override
  public String toString() {
    return "CoinsPHDepositAddress{"
        + "coin='"
        + coin
        + '\''
        + ", address='"
        + address
        + '\''
        + ", addressTag='"
        + addressTag
        + '\''
        + '}';
  }
}