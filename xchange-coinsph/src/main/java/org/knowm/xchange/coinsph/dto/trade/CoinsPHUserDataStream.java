package org.knowm.xchange.coinsph.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User data stream information from Coins.ph
 */
public class CoinsPHUserDataStream {

  private final String listenKey;

  public CoinsPHUserDataStream(@JsonProperty("listenKey") String listenKey) {
    this.listenKey = listenKey;
  }

  public String getListenKey() {
    return listenKey;
  }

  @Override
  public String toString() {
    return "CoinsPHUserDataStream{" + "listenKey='" + listenKey + '\'' + '}';
  }
}