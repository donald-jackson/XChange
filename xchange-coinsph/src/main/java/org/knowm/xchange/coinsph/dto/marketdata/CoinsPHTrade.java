package org.knowm.xchange.coinsph.dto.marketdata;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Trade data from Coins.ph
 */
public class CoinsPHTrade {

  private final long id;
  private final BigDecimal price;
  private final BigDecimal qty;
  private final BigDecimal quoteQty;
  private final long time;
  private final boolean isBuyerMaker;
  private final boolean isBestMatch;

  public CoinsPHTrade(
      @JsonProperty("id") long id,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("qty") BigDecimal qty,
      @JsonProperty("quoteQty") BigDecimal quoteQty,
      @JsonProperty("time") long time,
      @JsonProperty("isBuyerMaker") boolean isBuyerMaker,
      @JsonProperty("isBestMatch") boolean isBestMatch) {
    this.id = id;
    this.price = price;
    this.qty = qty;
    this.quoteQty = quoteQty;
    this.time = time;
    this.isBuyerMaker = isBuyerMaker;
    this.isBestMatch = isBestMatch;
  }

  public long getId() {
    return id;
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

  public long getTime() {
    return time;
  }

  public boolean isBuyerMaker() {
    return isBuyerMaker;
  }

  public boolean isBestMatch() {
    return isBestMatch;
  }

  @Override
  public String toString() {
    return "CoinsPHTrade{"
        + "id="
        + id
        + ", price="
        + price
        + ", qty="
        + qty
        + ", time="
        + time
        + ", isBuyerMaker="
        + isBuyerMaker
        + '}';
  }
}