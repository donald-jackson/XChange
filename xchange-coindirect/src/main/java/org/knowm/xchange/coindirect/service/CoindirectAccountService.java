package org.knowm.xchange.coindirect.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coindirect.CoindirectAdapters;
import org.knowm.xchange.coindirect.dto.account.CoindirectBalance;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;

public class CoindirectAccountService extends CoindirectAccountServiceRaw
    implements AccountService {
  /**
   * Constructor
   *
   * @param exchange
   */
  public CoindirectAccountService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    List<CoindirectBalance> coindirectBalances = listCoindirectBalances();

    Wallet wallet;
    Balance balance;

    List<Wallet> wallets = new ArrayList<>();

    for (CoindirectBalance coindirectBalance : coindirectBalances) {
      balance =
          new Balance(
              CoindirectAdapters.toCurrency(coindirectBalance.currency.code),
              coindirectBalance.total,
              coindirectBalance.available,
              coindirectBalance.reserved);
      wallet =
          new Wallet(
              String.valueOf(coindirectBalance.currency.code),
              coindirectBalance.currency.name,
              Arrays.asList(balance));
      wallets.add(wallet);
    }

    return new AccountInfo(wallets);
  }
}
