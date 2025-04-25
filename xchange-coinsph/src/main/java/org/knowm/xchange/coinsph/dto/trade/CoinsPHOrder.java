package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order information from Coins.ph
 */
public class CoinsPHOrder {

  private final String clientOrderId;
  private final BigDecimal cummulativeQuoteQty;
  private final BigDecimal executedQty;
  private final boolean isWorking;
  private final long orderId;
  private final BigDecimal origQty;
  private final BigDecimal origQuoteOrderQty;
  private final BigDecimal price;
  private final String side;
  private final String status;
  private final BigDecimal stopPrice;
  private final String symbol;
  private final long time;
  private final String timeInForce;
  private final String type;
  private final long updateTime;

  public CoinsPHOrder(
      @JsonProperty("clientOrderId") String clientOrderId,
      @JsonProperty("cummulativeQuoteQty") BigDecimal cummulativeQuoteQty,
      @JsonProperty("executedQty") BigDecimal executedQty,
      @JsonProperty("isWorking") boolean isWorking,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("origQty") BigDecimal origQty,
      @JsonProperty("origQuoteOrderQty") BigDecimal origQuoteOrderQty,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("side") String side,
      @JsonProperty("status") String status,
      @JsonProperty("stopPrice") BigDecimal stopPrice,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("time") long time,
      @JsonProperty("timeInForce") String timeInForce,
      @JsonProperty("type") String type,
      @JsonProperty("updateTime") long updateTime) {
    this.clientOrderId = clientOrderId;
    this.cummulativeQuoteQty = cummulativeQuoteQty;
    this.executedQty = executedQty;
    this.isWorking = isWorking;
    this.orderId = orderId;
    this.origQty = origQty;
    this.origQuoteOrderQty = origQuoteOrderQty;
    this.price = price;
    this.side = side;
    this.status = status;
    this.stopPrice = stopPrice;
    this.symbol = symbol;
    this.time = time;
    this.timeInForce = timeInForce;
    this.type = type;
    this.updateTime = updateTime;
  }

  public String getClientOrderId() {
    return clientOrderId;
  }

  public BigDecimal getCummulativeQuoteQty() {
    return cummulativeQuoteQty;
  }

  public BigDecimal getExecutedQty() {
    return executedQty;
  }

  public boolean isWorking() {
    return isWorking;
  }

  public long getOrderId() {
    return orderId;
  }

  public BigDecimal getOrigQty() {
    return origQty;
  }

  public BigDecimal getOrigQuoteOrderQty() {
    return origQuoteOrderQty;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getSide() {
    return side;
  }

  public String getStatus() {
    return status;
  }

  public BigDecimal getStopPrice() {
    return stopPrice;
  }

  public String getSymbol() {
    return symbol;
  }

  public long getTime() {
    return time;
  }

  public String getTimeInForce() {
    return timeInForce;
  }

  public String getType() {
    return type;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  @Override
  public String toString() {
    return "CoinsPHOrder{"
        + "orderId="
        + orderId
        + ", symbol='"
        + symbol
        + '\''
        + ", status='"
        + status
        + '\''
        + ", type='"
        + type
        + '\''
        + ", side='"
        + side
        + '\''
        + '}';
  }
}