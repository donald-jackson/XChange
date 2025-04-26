package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Withdraw response from Coins.ph */
@Getter
@ToString
public class CoinsPHWithdrawResponse {

  private final String id;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHWithdrawResponse(@JsonProperty("id") String id) {
    this.id = id;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
