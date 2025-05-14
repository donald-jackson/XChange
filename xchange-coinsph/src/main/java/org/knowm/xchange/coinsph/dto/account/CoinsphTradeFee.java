package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CoinsphTradeFee {

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("makerCommission")
  private BigDecimal makerCommission; // e.g., 0.001 for 0.1%

  @JsonProperty("takerCommission")
  private BigDecimal takerCommission; // e.g., 0.001 for 0.1%

  // The API response is an array of these objects.
  // Example:
  // [
  //   {
  //     "symbol": "BTCPHP",
  //     "makerCommission": "0.0010",
  //     "takerCommission": "0.0020"
  //   },
  //   {
  //     "symbol": "ETHPHP",
  //     "makerCommission": "0.0015",
  //     "takerCommission": "0.0025"
  //   }
  // ]
}