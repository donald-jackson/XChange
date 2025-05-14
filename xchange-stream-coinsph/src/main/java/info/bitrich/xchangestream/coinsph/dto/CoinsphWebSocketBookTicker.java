package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true) // To include fields from CoinsphWebSocketEvent
public class CoinsphWebSocketBookTicker extends CoinsphWebSocketEvent {

  @JsonProperty("u") private final long updateId; // Order book updateId
  @JsonProperty("s") private final String symbol;
  @JsonProperty("b") private final BigDecimal bidPrice;
  @JsonProperty("B") private final BigDecimal bidQty;
  @JsonProperty("a") private final BigDecimal askPrice;
  @JsonProperty("A") private final BigDecimal askQty;

  public CoinsphWebSocketBookTicker(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") long eventTime,
      @JsonProperty("u") long updateId,
      @JsonProperty("s") String symbol,
      @JsonProperty("b") BigDecimal bidPrice,
      @JsonProperty("B") BigDecimal bidQty,
      @JsonProperty("a") BigDecimal askPrice,
      @JsonProperty("A") BigDecimal askQty) {
    super(eventType, eventTime);
    this.updateId = updateId;
    this.symbol = symbol;
    this.bidPrice = bidPrice;
    this.bidQty = bidQty;
    this.askPrice = askPrice;
    this.askQty = askQty;
  }
}