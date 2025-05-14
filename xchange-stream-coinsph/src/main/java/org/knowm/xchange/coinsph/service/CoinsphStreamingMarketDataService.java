package org.knowm.xchange.coinsph.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.coinsph.CoinsphAdapters; // Will need streaming-specific adapters
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphWebSocketOrderBook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsphWebSocketTrade;
// import org.knowm.xchange.coinsph.dto.marketdata.CoinsphWebSocketTicker; // If there's a specific ticker stream
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinsphStreamingMarketDataService implements StreamingMarketDataService {

  private static final Logger LOG = LoggerFactory.getLogger(CoinsphStreamingMarketDataService.class);

  private final CoinsphStreamingService service;
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public CoinsphStreamingMarketDataService(CoinsphStreamingService service) {
    this.service = service;
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String symbol = CoinsphAdapters.toSymbol(currencyPair);
    // Channel name format needs to be verified from Coins.ph docs.
    // Example: "<symbol>@depth" or "<symbol>@depth<level>@<update_speed>"
    String channelName = symbol.toLowerCase() + "@depth"; // Placeholder
    LOG.info("Subscribing to order book channel: {}", channelName);

    return service
        .subscribeChannel(channelName)
        .map(
            jsonNode -> {
              // Assuming the "data" part of the message contains the order book payload
              CoinsphWebSocketOrderBook coinsphOrderBook =
                  mapper.treeToValue(jsonNode, CoinsphWebSocketOrderBook.class); // Or jsonNode.get("data")
              // TODO: Adapt CoinsphWebSocketOrderBook to XChange OrderBook
              // This will likely involve managing a local snapshot of the order book
              // and applying updates, or if it's full snapshots, direct adaptation.
              // For now, returning an empty OrderBook or throwing UnsupportedOperationException
              // return CoinsphStreamingAdapters.adaptOrderBook(coinsphOrderBook, currencyPair);
              throw new UnsupportedOperationException("Order book adaptation not yet implemented.");
            });
  }

  @Override
  public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
    String symbol = CoinsphAdapters.toSymbol(currencyPair);
    // Channel name for ticker, e.g., "<symbol>@ticker" or part of a combined stream
    String channelName = symbol.toLowerCase() + "@ticker"; // Placeholder
    LOG.info("Subscribing to ticker channel: {}", channelName);

    return service
        .subscribeChannel(channelName)
        .map(
            jsonNode -> {
              // CoinsphWebSocketTicker coinsphTicker = mapper.treeToValue(jsonNode, CoinsphWebSocketTicker.class);
              // return CoinsphStreamingAdapters.adaptTicker(coinsphTicker);
              throw new UnsupportedOperationException("Ticker stream not yet implemented or adapted.");
            });
  }

  @Override
  public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
    String symbol = CoinsphAdapters.toSymbol(currencyPair);
    // Channel name for trades, e.g., "<symbol>@trade"
    String channelName = symbol.toLowerCase() + "@trade"; // Placeholder
    LOG.info("Subscribing to trades channel: {}", channelName);

    return service
        .subscribeChannel(channelName)
        .map(
            jsonNode -> {
              CoinsphWebSocketTrade coinsphTrade =
                  mapper.treeToValue(jsonNode, CoinsphWebSocketTrade.class); // Or jsonNode.get("data")
              // TODO: Adapt CoinsphWebSocketTrade to XChange Trade
              // return CoinsphStreamingAdapters.adaptTrade(coinsphTrade);
              throw new UnsupportedOperationException("Trade adaptation not yet implemented.");
            });
  }
}