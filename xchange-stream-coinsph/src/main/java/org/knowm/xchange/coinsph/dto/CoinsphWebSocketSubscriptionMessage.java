package org.knowm.xchange.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinsphWebSocketSubscriptionMessage {
  @JsonProperty("method")
  private String method; // e.g., "SUBSCRIBE", "UNSUBSCRIBE"

  @JsonProperty("params")
  private String[] params; // e.g., ["btcusdt@depth", "btcusdt@trade"]

  @JsonProperty("id")
  private Integer id; // Request ID
}