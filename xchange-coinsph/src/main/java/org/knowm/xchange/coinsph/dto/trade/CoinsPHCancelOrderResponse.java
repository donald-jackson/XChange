package org.knowm.xchange.coinsph.dto.trade;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Cancel order response from Coins.ph */
@Getter
@ToString(of = {"symbol", "orderId", "clientOrderId", "status"})
public class CoinsPHCancelOrderResponse {

  private final String symbol;
  private final long orderId;
  private final String clientOrderId;
  private final BigDecimal price;
  private final BigDecimal origQty;
  private final BigDecimal executedQty;
  private final BigDecimal cummulativeQuoteQty;
  private final String status;
  private final String timeInForce;
  private final String type;
  private final String side;
  private final BigDecimal stopPrice;
  private final BigDecimal origQuoteOrderQty;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHCancelOrderResponse(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("clientOrderId") String clientOrderId,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("origQty") BigDecimal origQty,
      @JsonProperty("executedQty") BigDecimal executedQty,
      @JsonProperty("cummulativeQuoteQty") BigDecimal cummulativeQuoteQty,
      @JsonProperty("status") String status,
      @JsonProperty("timeInForce") String timeInForce,
      @JsonProperty("type") String type,
      @JsonProperty("side") String side,
      @JsonProperty("stopPrice") BigDecimal stopPrice,
      @JsonProperty("origQuoteOrderQty") BigDecimal origQuoteOrderQty) {
    this.symbol = symbol;
    this.orderId = orderId;
    this.clientOrderId = clientOrderId;
    this.price = price;
    this.origQty = origQty;
    this.executedQty = executedQty;
    this.cummulativeQuoteQty = cummulativeQuoteQty;
    this.status = status;
    this.timeInForce = timeInForce;
    this.type = type;
    this.side = side;
    this.stopPrice = stopPrice;
    this.origQuoteOrderQty = origQuoteOrderQty;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
