package org.knowm.xchange.coinsph.dto.trade;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Order response from Coins.ph */
@Getter
@ToString(of = {"symbol", "orderId", "clientOrderId", "status", "type", "side"})
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
  private final Map<String, Object> additionalProperties = new HashMap<>();

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

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  /** Order fill information */
  @Getter
  @ToString
  public static class CoinsPHOrderFill {
    private final BigDecimal price;
    private final BigDecimal qty;
    private final BigDecimal commission;
    private final String commissionAsset;
    private final String tradeId;
    private final Map<String, Object> additionalProperties = new HashMap<>();

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

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }
  }
}
