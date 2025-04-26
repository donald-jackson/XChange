package org.knowm.xchange.coinsph.service;

import java.io.IOException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinsph.CoinsPHAdapters;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;

/** Implementation of the market data service for Coins.ph */
public class CoinsPHMarketDataService extends CoinsPHMarketDataServiceRaw
    implements MarketDataService {

  /**
   * Constructor
   *
   * @param exchange the exchange to use
   */
  public CoinsPHMarketDataService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public Ticker getTicker(Instrument instrument, Object... args) throws IOException {
    return CoinsPHAdapters.adaptTicker(getCoinsPHTicker(instrument), instrument);
  }

  @Override
  public Ticker getTicker(CurrencyPair instrument, Object... args) throws IOException {
    return getTicker((Instrument) instrument, args);
  }

  @Override
  public OrderBook getOrderBook(Instrument instrument, Object... args) throws IOException {
    Integer limit = null;
    if (args != null && args.length > 0) {
      if (args[0] instanceof Integer) {
        limit = (Integer) args[0];
      }
    }
    return CoinsPHAdapters.adaptOrderBook(getCoinsPHOrderbook(instrument, limit), instrument);
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair instrument, Object... args) throws IOException {
    return getOrderBook((Instrument) instrument, args);
  }

  @Override
  public Trades getTrades(Instrument instrument, Object... args) throws IOException {
    Integer limit = null;
    if (args != null && args.length > 0) {
      if (args[0] instanceof Integer) {
        limit = (Integer) args[0];
      }
    }
    return CoinsPHAdapters.adaptTrades(getCoinsPHTrades(instrument, limit), instrument);
  }

  @Override
  public Trades getTrades(CurrencyPair instrument, Object... args) throws IOException {
    return getTrades((Instrument) instrument, args);
  }
}
