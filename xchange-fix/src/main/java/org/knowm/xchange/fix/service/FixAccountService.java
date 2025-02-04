package org.knowm.xchange.fix.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public class FixAccountService extends FixBaseService implements AccountService {

    public FixAccountService(Exchange exchange) {
        super(exchange);
    }

    @Override
    public AccountInfo getAccountInfo() throws IOException {
        CompletableFuture<AccountInfo> future = new CompletableFuture<>();
        // TODO: Implement account info request using FIX protocol
        return future.join();
    }

    @Override
    public String withdrawFunds(Currency currency, BigDecimal amount, String address) throws IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        // TODO: Implement withdrawal request using FIX protocol
        return future.join();
    }

    @Override
    public String withdrawFunds(WithdrawFundsParams params) throws IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        // TODO: Implement withdrawal request using FIX protocol
        return future.join();
    }

    @Override
    public String requestDepositAddress(Currency currency, String... args) throws IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        // TODO: Implement deposit address request using FIX protocol
        return future.join();
    }

    @Override
    public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
        CompletableFuture<List<FundingRecord>> future = new CompletableFuture<>();
        // TODO: Implement funding history request using FIX protocol
        return future.join();
    }
}