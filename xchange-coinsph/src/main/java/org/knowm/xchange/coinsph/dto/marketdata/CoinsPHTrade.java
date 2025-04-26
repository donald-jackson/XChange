package org.knowm.xchange.coinsph.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/** Trade data from Coins.ph */
@Getter
@ToString(of = {"id", "price", "qty", "time", "isBuyerMaker"})
public class CoinsPHTrade {

  private final long id;
  private final BigDecimal price;
  private final BigDecimal qty;
  private final BigDecimal quoteQty;
  private final long time;
  private final boolean isBuyerMaker;
  private final boolean isBestMatch;
  private final Map<String, Object> additionalProperties = new HashMap<>();

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

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
