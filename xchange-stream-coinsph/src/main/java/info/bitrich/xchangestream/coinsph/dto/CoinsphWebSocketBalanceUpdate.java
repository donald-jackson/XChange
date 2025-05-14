package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoinsphWebSocketBalanceUpdate extends CoinsphWebSocketEvent {

  private final String asset;
  private final BigDecimal balanceDelta;
  private final long transactionTime;

  public CoinsphWebSocketBalanceUpdate(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") long eventTime,
      @JsonProperty("a") String asset,
      @JsonProperty("d") BigDecimal balanceDelta,
      @JsonProperty("T") long transactionTime) {
    super(eventType, eventTime);
    this.asset = asset;
    this.balanceDelta = balanceDelta;
    this.transactionTime = transactionTime;
  }
}