package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.CoinsPH;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

/** Implementation of the market data service for Coins.ph */
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
   * Get ticker for a specific instrument
   *
   * @param instrument the instrument
   * @return the ticker
   * @throws IOException if an error occurs
   */
  public CoinsPHTicker24h getCoinsPHTicker(Instrument instrument) throws IOException {
    return coinsPH.get24hrTicker(formatSymbol(instrument));
  }

  /**
   * Get ticker for a specific symbol
   *
   * @param symbol the symbol
   * @return the ticker
   * @throws IOException if an error occurs
   */
  public CoinsPHTicker24h getCoinsPH24hrTicker(String symbol) throws IOException {
    return coinsPH.get24hrTicker(symbol);
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
   * Get order book for a specific instrument
   *
   * @param instrument the instrument
   * @param limit the limit of orders to return
   * @return the order book
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderbook getCoinsPHOrderbook(Instrument instrument, Integer limit)
      throws IOException {
    return coinsPH.getOrderBook(formatSymbol(instrument), limit);
  }

  /**
   * Get order book for a specific symbol
   *
   * @param symbol the symbol
   * @param limit the limit of orders to return
   * @return the order book
   * @throws IOException if an error occurs
   */
  public CoinsPHOrderbook getCoinsPHOrderBook(String symbol, Integer limit) throws IOException {
    return coinsPH.getOrderBook(symbol, limit);
  }

  /**
   * Get trades for a specific instrument
   *
   * @param instrument the instrument
   * @param limit the limit of trades to return
   * @return the trades
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHTrades(Instrument instrument, Integer limit)
      throws IOException {
    return coinsPH.getTrades(formatSymbol(instrument), limit);
  }

  /**
   * Get trades for a specific symbol
   *
   * @param symbol the symbol
   * @param limit the limit of trades to return
   * @return the trades
   * @throws IOException if an error occurs
   */
  public List<CoinsPHTrade> getCoinsPHTrades(String symbol, Integer limit) throws IOException {
    return coinsPH.getTrades(symbol, limit);
  }

  /**
   * Get server time
   *
   * @return the server time
   * @throws IOException if an error occurs
   */
  public CoinsPH.CoinsPHServerTime getCoinsPHServerTime() throws IOException {
    return coinsPH.getServerTime();
  }

  /**
   * Get exchange info
   *
   * @return the exchange info
   * @throws IOException if an error occurs
   */
  public CoinsPH.CoinsPHExchangeInfo getCoinsPHExchangeInfo() throws IOException {
    return coinsPH.getExchangeInfo();
  }

  /**
   * Format instrument to Coins.ph symbol format
   *
   * @param instrument the instrument
   * @return the formatted symbol
   */
  private String formatSymbol(Instrument instrument) {
    if (instrument instanceof CurrencyPair) {
      CurrencyPair currencyPair = (CurrencyPair) instrument;
      return currencyPair.getBase().getCurrencyCode() + currencyPair.getCounter().getCurrencyCode();
    }
    throw new IllegalArgumentException("Instrument must be a CurrencyPair");
  }
}
