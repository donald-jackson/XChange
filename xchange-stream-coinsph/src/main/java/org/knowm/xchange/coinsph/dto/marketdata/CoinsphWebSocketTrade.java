package org.knowm.xchange.coinsph.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CoinsphWebSocketTrade {
  // These are common fields for a trade event. Verify against Coins.ph WebSocket documentation.
  @JsonProperty("e")
  private String eventType; // e.g., "trade"

  @JsonProperty("E")
  private Long eventTime;

  @JsonProperty("s")
  private String symbol;

  @JsonProperty("t")
  private Long tradeId;

  @JsonProperty("p")
  private BigDecimal price;

  @JsonProperty("q")
  private BigDecimal quantity;

  @JsonProperty("b")
  private Long buyerOrderId;

  @JsonProperty("a")
  private Long sellerOrderId;

  @JsonProperty("T")
  private Long tradeTime;

  @JsonProperty("m")
  private Boolean buyerIsMaker;

  // Add other fields as per Coins.ph WebSocket trade stream
}