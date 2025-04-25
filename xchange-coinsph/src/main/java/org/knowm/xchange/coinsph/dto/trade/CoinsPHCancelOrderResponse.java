package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cancel order response from Coins.ph
 */
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

  public String getSymbol() {
    return symbol;
  }

  public long getOrderId() {
    return orderId;
  }

  public String getClientOrderId() {
    return clientOrderId;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getOrigQty() {
    return origQty;
  }

  public BigDecimal getExecutedQty() {
    return executedQty;
  }

  public BigDecimal getCummulativeQuoteQty() {
    return cummulativeQuoteQty;
  }

  public String getStatus() {
    return status;
  }

  public String getTimeInForce() {
    return timeInForce;
  }

  public String getType() {
    return type;
  }

  public String getSide() {
    return side;
  }

  public BigDecimal getStopPrice() {
    return stopPrice;
  }

  public BigDecimal getOrigQuoteOrderQty() {
    return origQuoteOrderQty;
  }

  @Override
  public String toString() {
    return "CoinsPHCancelOrderResponse{"
        + "symbol='"
        + symbol
        + '\''
        + ", orderId="
        + orderId
        + ", clientOrderId='"
        + clientOrderId
        + '\''
        + ", status='"
        + status
        + '\''
        + '}';
  }
}