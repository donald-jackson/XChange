package org.knowm.xchange.coinsph.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Withdraw response from Coins.ph
 */
public class CoinsPHWithdrawResponse {

  private final String id;

  public CoinsPHWithdrawResponse(@JsonProperty("id") String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "CoinsPHWithdrawResponse{" + "id='" + id + '\'' + '}';
  }
}