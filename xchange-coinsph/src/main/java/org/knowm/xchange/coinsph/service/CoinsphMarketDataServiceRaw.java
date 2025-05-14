package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.coinsph.CoinsphExchange;
import org.knowm.xchange.coinsph.dto.CoinsphException;
import org.knowm.xchange.coinsph.CoinsphAdapters;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphTicker;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphOrderBook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphPublicTrade;
import org.knowm.xchange.currency.CurrencyPair;
import java.util.List; // For list responses like getTrades and getTicker24hr (all)

public class CoinsphMarketDataServiceRaw extends CoinsphBaseService {

  protected CoinsphMarketDataServiceRaw(
      CoinsphExchange exchange, ResilienceRegistries resilienceRegistries) {
    super(exchange, resilienceRegistries);
  }

  public CoinsphTicker getCoinsphTicker(CurrencyPair currencyPair)
      throws IOException, CoinsphException {
    return decorateApiCall(() -> coinsph.getTicker24hr(CoinsphAdapters.toSymbol(currencyPair)))
        // .withRetry(retry("ticker")) // Define in CoinsphResilience
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER)) // Define in CoinsphResilience
        .call();
  }

  public List<CoinsphTicker> getCoinsphTickers() throws IOException, CoinsphException {
    return decorateApiCall(() -> coinsph.getTicker24hr())
        // .withRetry(retry("tickers")) // Define in CoinsphResilience
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER_MEDIUM)) // Define in CoinsphResilience, potentially different weight
        .call();
  }

  public CoinsphOrderBook getCoinsphOrderBook(CurrencyPair currencyPair, Integer limit)
      throws IOException, CoinsphException {
    return decorateApiCall(
            () -> coinsph.getOrderBook(CoinsphAdapters.toSymbol(currencyPair), limit))
        // .withRetry(retry("orderBook")) // Define in CoinsphResilience
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER)) // Define in CoinsphResilience
        .call();
  }

  public List<CoinsphPublicTrade> getCoinsphTrades(
      CurrencyPair currencyPair, Integer limit) // Coins.ph API uses 'limit', not 'fromId' for public trades
      throws IOException, CoinsphException {
    return decorateApiCall(
            () ->
                coinsph.getTrades(
                    CoinsphAdapters.toSymbol(currencyPair),
                    limit))
        // .withRetry(retry("trades")) // Define in CoinsphResilience
        // .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER)) // Define in CoinsphResilience
        .call();
  }

  // ping, time, exchangeInfo are usually called from CoinsphExchange directly or via base Exchange methods.
  // For example, exchange.getExchangeMetaData() would call exchange.getPublicApi().exchangeInfo()
  // and exchange.getRemoteInitTimer().ping()
}