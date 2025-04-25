package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Trade information from Coins.ph
 */
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

  public String getSymbol() {
    return symbol;
  }

  public long getId() {
    return id;
  }

  public long getOrderId() {
    return orderId;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getQty() {
    return qty;
  }

  public BigDecimal getQuoteQty() {
    return quoteQty;
  }

  public BigDecimal getCommission() {
    return commission;
  }

  public String getCommissionAsset() {
    return commissionAsset;
  }

  public long getTime() {
    return time;
  }

  public boolean isBuyer() {
    return isBuyer;
  }

  public boolean isMaker() {
    return isMaker;
  }

  public boolean isBestMatch() {
    return isBestMatch;
  }

  @Override
  public String toString() {
    return "CoinsPHTrade{"
        + "symbol='"
        + symbol
        + '\''
        + ", id="
        + id
        + ", orderId="
        + orderId
        + ", price="
        + price
        + ", qty="
        + qty
        + ", time="
        + time
        + '}';
  }
}