package org.knowm.xchange.coinsph.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphOrderBookEntry; // Re-use from REST DTO if applicable

@Data
@NoArgsConstructor
public class CoinsphWebSocketOrderBook {
  // These are common fields for a depth update event. Verify against Coins.ph WebSocket documentation.
  @JsonProperty("e")
  private String eventType; // e.g., "depthUpdate"

  @JsonProperty("E")
  private Long eventTime;

  @JsonProperty("s")
  private String symbol;

  @JsonProperty("U")
  private Long firstUpdateId; // First update ID in event

  @JsonProperty("u")
  private Long finalUpdateId; // Final update ID in event

  // Bids to be updated: [Price Level, Quantity]
  @JsonProperty("b")
  private List<CoinsphOrderBookEntry> bids;

  // Asks to be updated: [Price Level, Quantity]
  @JsonProperty("a")
  private List<CoinsphOrderBookEntry> asks;

  // Add other fields as per Coins.ph WebSocket depth stream
  // e.g., previous final update ID for sequencing, if provided.
}