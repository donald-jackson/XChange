package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoinsphWebSocketSubscriptionMessage {
  @JsonProperty("method")
  private final String method;

  @JsonProperty("params")
  private final String[] params;

  @JsonProperty("id")
  private final int id;
}