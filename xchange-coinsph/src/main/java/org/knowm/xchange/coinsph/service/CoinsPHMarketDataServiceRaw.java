package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * Implementation of the market data service for Coins.ph
 */
public class CoinsPHMarketDataServiceRaw extends CoinsPHBaseService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  /**
   * Get ticker for a specific currency pair
   *
   * @param currencyPair the currency pair
   * @return the ticker
   * @throws IOException if an error occurs
   */
  public CoinsPHTicker24h getCoinsPHTicker(CurrencyPair currencyPair) throws IOException {
    return coinsPH.get24hrTicker(formatSymbol(currencyPair));
  }

  /**
   * Get all tickers
   *
   * @return the tickers
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTicker24h> getCoinsPHAllTickers() throws IOException {
    return coinsPH.getAll24hrTickers();
  }

  /**
   * Get order book for a specific currency pair
   *
   * @param currencyPair the currency pair
   * @param limit the limit of orders to return
   * @return the order book
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderbook getCoinsPHOrderbook(CurrencyPair currencyPair, Integer limit) throws IOException {
    return coinsPH.getOrderBook(formatSymbol(currencyPair), limit);
  }

  /**
   * Get trades for a specific currency pair
   *
   * @param currencyPair the currency pair
   * @param limit the limit of trades to return
   * @return the trades
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHTrades(CurrencyPair currencyPair, Integer limit) throws IOException {
    return coinsPH.getTrades(formatSymbol(currencyPair), limit);
  }

  /**
   * Format currency pair to Coins.ph symbol format
   *
   * @param currencyPair the currency pair
   * @return the formatted symbol
   */
  private String formatSymbol(CurrencyPair currencyPair) {
    return currencyPair.getBase().getCurrencyCode() + currencyPair.getCounter().getCurrencyCode();
  }
}