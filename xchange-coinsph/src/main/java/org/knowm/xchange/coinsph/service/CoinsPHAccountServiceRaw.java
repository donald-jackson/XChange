package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.math.BigDecimal;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.account.CoinsPHAccountInfo;
import org.knowm.xchange.coinsph.dto.account.CoinsPHDepositAddress;
import org.knowm.xchange.coinsph.dto.account.CoinsPHWithdrawResponse;
import org.knowm.xchange.currency.Currency;

/** Implementation of the account service for Coins.ph */
public class CoinsPHAccountServiceRaw extends CoinsPHBaseService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  /**
   * Get account information
   *
   * @return the account information
   * @throws IOException if an error occurs
   */
  public CoinsPHAccountInfo getCoinsPHAccountInfo() throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getAccountInfo(
        apiKey, recvWindow, timestamp, coinsPHSignatureCreator);
  }

  /**
   * Get deposit address for a specific currency
   *
   * @param currency the currency
   * @param network the network
   * @return the deposit address
   * @throws IOException if an error occurs
   */
  public CoinsPHDepositAddress getCoinsPHDepositAddress(Currency currency, String network)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.getDepositAddress(
        apiKey,
        currency.getCurrencyCode(),
        network,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Withdraw funds
   *
   * @param currency the currency
   * @param network the network
   * @param address the address
   * @param addressTag the address tag
   * @param amount the amount
   * @param withdrawOrderId the withdraw order id
   * @return the withdraw response
   * @throws IOException if an error occurs
   */
  public CoinsPHWithdrawResponse withdraw(
      Currency currency,
      String network,
      String address,
      String addressTag,
      BigDecimal amount,
      String withdrawOrderId)
      throws IOException {
    long timestamp = exchange.getNonceFactory().createValue();
    long recvWindow = getRecvWindow();

    return coinsPHAuthenticated.withdraw(
        apiKey,
        currency.getCurrencyCode(),
        network,
        address,
        addressTag,
        amount.toPlainString(),
        withdrawOrderId,
        recvWindow,
        timestamp,
        coinsPHSignatureCreator);
  }

  /**
   * Get receive window
   *
   * @return the receive window
   */
  private Long getRecvWindow() {
    return 5000L;
  }
}
