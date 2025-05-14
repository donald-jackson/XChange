package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.coinsph.CoinsphAdapters;
import org.knowm.xchange.coinsph.CoinsphExchange;
import org.knowm.xchange.coinsph.dto.CoinsphException;
import org.knowm.xchange.coinsph.dto.account.CoinsphAccount;
import org.knowm.xchange.coinsph.dto.account.CoinsphTradeFee; // For trade fees
import org.knowm.xchange.currency.Currency;
// import org.knowm.xchange.dto.account.DynamicTradingFees; // Class not found, using Map<Instrument, Fee>
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public class CoinsphAccountService extends CoinsphAccountServiceRaw implements AccountService {

  public CoinsphAccountService(
      CoinsphExchange exchange, ResilienceRegistries resilienceRegistries) {
    super(exchange, resilienceRegistries);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException, CoinsphException {
    CoinsphAccount coinsphAccount = super.getCoinsphAccount();
    return CoinsphAdapters.adaptAccountInfo(coinsphAccount, exchange.getExchangeSpecification().getUserName());
  }

  @Override
  public String withdrawFunds(Currency currency, BigDecimal amount, String address)
      throws IOException, CoinsphException {
    // TODO: Implement withdrawFunds if API supports it
    // return super.withdraw(currency.getCurrencyCode(), address, amount, null, null);
    throw new UnsupportedOperationException("withdrawFunds not implemented yet");
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException, CoinsphException {
    // TODO: Implement withdrawFunds with params if API supports it
    throw new UnsupportedOperationException("withdrawFunds not implemented yet");
  }

  @Override
  public String requestDepositAddress(Currency currency, String... args)
      throws IOException, CoinsphException {
    // TODO: Implement requestDepositAddress if API supports it
    // return super.requestDepositAddress(currency);
    throw new UnsupportedOperationException("requestDepositAddress not implemented yet");
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    // TODO: Implement createFundingHistoryParams if API supports funding history
    throw new UnsupportedOperationException("createFundingHistoryParams not implemented yet");
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params)
      throws IOException, CoinsphException {
    // TODO: Implement getFundingHistory if API supports it
    throw new UnsupportedOperationException("getFundingHistory not implemented yet");
  }

  /**
   * Get the dynamic trading fees for all symbols.
   *
   * @return DynamicTradingFees
   * @throws IOException
   * @throws CoinsphException
   */
  @Override
  public java.util.Map<org.knowm.xchange.instrument.Instrument, org.knowm.xchange.dto.account.Fee> getTradingFees() throws IOException, CoinsphException {
    List<CoinsphTradeFee> fees = super.getCoinsphTradeFees();
    return CoinsphAdapters.adaptTradeFees(fees);
  }
}