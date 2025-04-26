package org.knowm.xchange.coinsph.dto.trade;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** New order request for Coins.ph */
@Getter
@ToString
public class CoinsPHNewOrder {

  private final String symbol;
  private final String side;
  private final String type;
  private final String timeInForce;
  private final BigDecimal quantity;
  private final BigDecimal quoteOrderQty;
  private final BigDecimal price;
  private final String newClientOrderId;
  private final BigDecimal stopPrice;
  private final String newOrderRespType;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHNewOrder(
      String symbol,
      String side,
      String type,
      String timeInForce,
      BigDecimal quantity,
      BigDecimal quoteOrderQty,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      String newOrderRespType) {
    this.symbol = symbol;
    this.side = side;
    this.type = type;
    this.timeInForce = timeInForce;
    this.quantity = quantity;
    this.quoteOrderQty = quoteOrderQty;
    this.price = price;
    this.newClientOrderId = newClientOrderId;
    this.stopPrice = stopPrice;
    this.newOrderRespType = newOrderRespType;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
