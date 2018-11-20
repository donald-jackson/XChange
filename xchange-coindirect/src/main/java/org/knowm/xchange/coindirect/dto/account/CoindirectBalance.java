package org.knowm.xchange.coindirect.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import org.knowm.xchange.coindirect.dto.meta.CoindirectCurrency;

public class CoindirectBalance {
  public final BigDecimal available;
  public final CoindirectCurrency currency;
  public final BigDecimal reserved;
  public final BigDecimal total;

  public CoindirectBalance(
      @JsonProperty("available") BigDecimal available,
      @JsonProperty("currency") CoindirectCurrency currency,
      @JsonProperty("reserved") BigDecimal reserved,
      @JsonProperty("total") BigDecimal total) {
    this.available = available;
    this.currency = currency;
    this.reserved = reserved;
    this.total = total;
  }

  @Override
  public String toString() {
    return "CoindirectBalance{"
        + "available="
        + available
        + ", currency="
        + currency
        + ", reserved="
        + reserved
        + ", total="
        + total
        + '}';
  }
}
