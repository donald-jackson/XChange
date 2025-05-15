package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoinsphWebSocketBalanceUpdate { // Removed "extends CoinsphWebSocketEvent"

  private final String eventType; // e - Added
  private final long eventTime;   // E - Added
  private final String asset;     // a
  private final BigDecimal balanceDelta; // d
  private final long transactionTime;    // T (Coins.ph calls this "Clear Time" for balanceUpdate)

  public CoinsphWebSocketBalanceUpdate(
      @JsonProperty("e") String eventType,    // Added
      @JsonProperty("E") long eventTime,      // Added
      @JsonProperty("a") String asset,
      @JsonProperty("d") BigDecimal balanceDelta,
      @JsonProperty("T") long transactionTime) {
    this.eventType = eventType; // Added
    this.eventTime = eventTime;   // Added
    this.asset = asset;
    this.balanceDelta = balanceDelta;
    this.transactionTime = transactionTime;
  }
}