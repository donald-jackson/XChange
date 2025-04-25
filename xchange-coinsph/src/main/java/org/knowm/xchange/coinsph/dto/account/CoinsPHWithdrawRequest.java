package org.knowm.xchange.coinsph.dto.account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * Withdraw request for Coins.ph
 */
@Getter
@ToString
public class CoinsPHWithdrawRequest {

  private final String coin;
  private final String network;
  private final String address;
  private final String addressTag;
  private final BigDecimal amount;
  private final String withdrawOrderId;
  private final Map<String, Object> additionalProperties = new HashMap<>();

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
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}