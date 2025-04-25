package org.knowm.xchange.coinsph.dto.account;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Balance information from Coins.ph
 */
public class CoinsPHBalance {

  private final String asset;
  private final BigDecimal free;
  private final BigDecimal locked;

  public CoinsPHBalance(
      @JsonProperty("asset") String asset,
      @JsonProperty("free") BigDecimal free,
      @JsonProperty("locked") BigDecimal locked) {
    this.asset = asset;
    this.free = free;
    this.locked = locked;
  }

  public String getAsset() {
    return asset;
  }

  public BigDecimal getFree() {
    return free;
  }

  public BigDecimal getLocked() {
    return locked;
  }

  @Override
  public String toString() {
    return "CoinsPHBalance{"
        + "asset='"
        + asset
        + '\''
        + ", free="
        + free
        + ", locked="
        + locked
        + '}';
  }
}