package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoinsphWebSocketOutboundAccountPosition {

  private final String eventType; // e
  private final long eventTime; // E
  private final long accountLastUpdateTime; // u
  private final List<CoinsphWebSocketBalance> balances; // B
  private final String accountEmail; // em (optional)

  public CoinsphWebSocketOutboundAccountPosition(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") long eventTime,
      @JsonProperty("u") long accountLastUpdateTime,
      @JsonProperty("B") List<CoinsphWebSocketBalance> balances,
      @JsonProperty("em") String accountEmail) {
    this.eventType = eventType;
    this.eventTime = eventTime;
    this.accountLastUpdateTime = accountLastUpdateTime;
    this.balances = balances;
    this.accountEmail = accountEmail;
  }

  @Getter
  @ToString
  public static class CoinsphWebSocketBalance {
    private final String asset; // a
    private final BigDecimal free; // f
    private final BigDecimal locked; // l

    public CoinsphWebSocketBalance(
        @JsonProperty("a") String asset,
        @JsonProperty("f") BigDecimal free,
        @JsonProperty("l") BigDecimal locked) {
      this.asset = asset;
      this.free = free;
      this.locked = locked;
    }
  }
}