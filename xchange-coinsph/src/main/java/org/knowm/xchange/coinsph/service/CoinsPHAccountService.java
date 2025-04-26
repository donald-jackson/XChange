package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.account.CoinsPHAccountInfo;
import org.knowm.xchange.coinsph.dto.account.CoinsPHBalance;
import org.knowm.xchange.coinsph.dto.account.CoinsPHDepositAddress;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.DefaultWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

/** Implementation of the account service for Coins.ph */
public class CoinsPHAccountService extends CoinsPHAccountServiceRaw implements AccountService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHAccountService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    CoinsPHAccountInfo accountInfo = getCoinsPHAccountInfo();
    List<Balance> balances = new ArrayList<>();

    for (CoinsPHBalance balance : accountInfo.getBalances()) {
      balances.add(
          new Balance.Builder()
              .currency(new Currency(balance.getAsset()))
              .available(balance.getFree())
              .frozen(balance.getLocked())
              .total(balance.getFree().add(balance.getLocked()))
              .build());
    }

    return new AccountInfo(
        new Wallet.Builder().id(accountInfo.getAccountType()).balances(balances).build());
  }

  @Override
  public String withdrawFunds(Currency currency, BigDecimal amount, String address)
      throws IOException {
    return withdrawFunds(new DefaultWithdrawFundsParams(address, currency, amount));
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException {
    if (params instanceof DefaultWithdrawFundsParams) {
      DefaultWithdrawFundsParams defaultParams = (DefaultWithdrawFundsParams) params;
      return withdraw(
              defaultParams.getCurrency(),
              null, // Default network
              defaultParams.getAddress(),
              null, // No address tag
              defaultParams.getAmount(),
              null) // No withdraw order id
          .getId();
    }
    throw new IllegalArgumentException(
        "WithdrawFundsParams must be an instance of DefaultWithdrawFundsParams");
  }

  @Override
  public String requestDepositAddress(Currency currency, String... args) throws IOException {
    String network = null;
    if (args != null && args.length > 0) {
      network = args[0];
    }

    CoinsPHDepositAddress depositAddress = getCoinsPHDepositAddress(currency, network);
    return depositAddress.getAddress();
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
