package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order response from Coins.ph
 */
public class CoinsPHOrderResponse {

  private final String symbol;
  private final long orderId;
  private final String clientOrderId;
  private final long transactTime;
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
  private final List<CoinsPHOrderFill> fills;

  public CoinsPHOrderResponse(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("clientOrderId") String clientOrderId,
      @JsonProperty("transactTime") long transactTime,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("origQty") BigDecimal origQty,
      @JsonProperty("executedQty") BigDecimal executedQty,
      @JsonProperty("cummulativeQuoteQty") BigDecimal cummulativeQuoteQty,
      @JsonProperty("status") String status,
      @JsonProperty("timeInForce") String timeInForce,
      @JsonProperty("type") String type,
      @JsonProperty("side") String side,
      @JsonProperty("stopPrice") BigDecimal stopPrice,
      @JsonProperty("origQuoteOrderQty") BigDecimal origQuoteOrderQty,
      @JsonProperty("fills") List<CoinsPHOrderFill> fills) {
    this.symbol = symbol;
    this.orderId = orderId;
    this.clientOrderId = clientOrderId;
    this.transactTime = transactTime;
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
    this.fills = fills;
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

  public long getTransactTime() {
    return transactTime;
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

  public List<CoinsPHOrderFill> getFills() {
    return fills;
  }

  @Override
  public String toString() {
    return "CoinsPHOrderResponse{"
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
        + ", type='"
        + type
        + '\''
        + ", side='"
        + side
        + '\''
        + '}';
  }

  /**
   * Order fill information
   */
  public static class CoinsPHOrderFill {
    private final BigDecimal price;
    private final BigDecimal qty;
    private final BigDecimal commission;
    private final String commissionAsset;
    private final String tradeId;

    public CoinsPHOrderFill(
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("qty") BigDecimal qty,
        @JsonProperty("commission") BigDecimal commission,
        @JsonProperty("commissionAsset") String commissionAsset,
        @JsonProperty("tradeId") String tradeId) {
      this.price = price;
      this.qty = qty;
      this.commission = commission;
      this.commissionAsset = commissionAsset;
      this.tradeId = tradeId;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public BigDecimal getQty() {
      return qty;
    }

    public BigDecimal getCommission() {
      return commission;
    }

    public String getCommissionAsset() {
      return commissionAsset;
    }

    public String getTradeId() {
      return tradeId;
    }
  }
}