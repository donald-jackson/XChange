package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.ToString;

/**
 * Trade information from Coins.ph
 */
@Getter
@ToString(of = {"symbol", "id", "orderId", "price", "qty", "time"})
public class CoinsPHTrade {

  private final String symbol;
  private final long id;
  private final long orderId;
  private final BigDecimal price;
  private final BigDecimal qty;
  private final BigDecimal quoteQty;
  private final BigDecimal commission;
  private final String commissionAsset;
  private final long time;
  private final boolean isBuyer;
  private final boolean isMaker;
  private final boolean isBestMatch;
  private final Map<String, Object> additionalProperties = new HashMap<>();

  public CoinsPHTrade(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("id") long id,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("qty") BigDecimal qty,
      @JsonProperty("quoteQty") BigDecimal quoteQty,
      @JsonProperty("commission") BigDecimal commission,
      @JsonProperty("commissionAsset") String commissionAsset,
      @JsonProperty("time") long time,
      @JsonProperty("isBuyer") boolean isBuyer,
      @JsonProperty("isMaker") boolean isMaker,
      @JsonProperty("isBestMatch") boolean isBestMatch) {
    this.symbol = symbol;
    this.id = id;
    this.orderId = orderId;
    this.price = price;
    this.qty = qty;
    this.quoteQty = quoteQty;
    this.commission = commission;
    this.commissionAsset = commissionAsset;
    this.time = time;
    this.isBuyer = isBuyer;
    this.isMaker = isMaker;
    this.isBestMatch = isBestMatch;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}