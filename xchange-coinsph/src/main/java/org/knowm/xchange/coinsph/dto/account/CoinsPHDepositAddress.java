package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Deposit address information from Coins.ph */
@Getter
@ToString
public class CoinsPHDepositAddress {

  private final String coin;
  private final String address;
  private final String addressTag;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHDepositAddress(
      @JsonProperty("coin") String coin,
      @JsonProperty("address") String address,
      @JsonProperty("addressTag") String addressTag) {
    this.coin = coin;
    this.address = address;
    this.addressTag = addressTag;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
