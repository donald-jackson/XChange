package org.knowm.xchange.coinsph.dto.trade;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * User data stream information from Coins.ph
 */
@Getter
@ToString
public class CoinsPHUserDataStream {

  private final String listenKey;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHUserDataStream(@JsonProperty("listenKey") String listenKey) {
    this.listenKey = listenKey;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}