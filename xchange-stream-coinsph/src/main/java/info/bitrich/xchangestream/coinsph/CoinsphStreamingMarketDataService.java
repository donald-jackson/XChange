package info.bitrich.xchangestream.coinsph;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketAggTrade;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketDepth;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketTicker;
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketBookTicker;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.rxjava3.core.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import info.bitrich.xchangestream.coinsph.CoinsphStreamingAdapters; // Added

public class CoinsphStreamingMarketDataService implements StreamingMarketDataService {

  private final CoinsphStreamingService service;
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public CoinsphStreamingMarketDataService(CoinsphStreamingService service) {
    this.service = service;
  }

  private String getChannelName(CurrencyPair currencyPair, String streamType) {
    return CoinsphStreamingAdapters.getChannelName(currencyPair, streamType); // To be created in Adapters
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String channelName = getChannelName(currencyPair, "depth"); // e.g. btcusdt@depth
    // Optional: Process args for depth levels or update speed if API supports
    // e.g. String channelName = getChannelName(currencyPair, "depth" + (args.length > 0 ? args[0] : ""));
    
    return service
        .subscribeChannel(channelName, args)
        .map(node -> mapper.treeToValue(node, CoinsphWebSocketDepth.class))
        .map(depth -> CoinsphStreamingAdapters.adaptOrderBook(depth, currencyPair)); // To be created
  }

  @Override
  public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
    // Coins.ph has aggTrade and trade streams. Defaulting to aggTrade for simplicity.
    // Use "trade" for individual raw trades if needed.
    String channelName = getChannelName(currencyPair, "aggTrade"); // e.g. btcusdt@aggTrade
    
    return service
        .subscribeChannel(channelName, args)
        .map(node -> mapper.treeToValue(node, CoinsphWebSocketAggTrade.class))
        .map(aggTrade -> CoinsphStreamingAdapters.adaptTrade(aggTrade)); // To be created
  }

  @Override
  public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
    String channelName = getChannelName(currencyPair, "ticker"); // e.g. btcusdt@ticker
    return service
        .subscribeChannel(channelName, args)
        .map(node -> mapper.treeToValue(node, info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketTicker.class))
        .map(CoinsphStreamingAdapters::adaptTicker);
  }
  
  @Override
  public Observable<Ticker> getBookTicker(CurrencyPair currencyPair, Object... args) {
    String channelName = getChannelName(currencyPair, "bookTicker"); // e.g. btcusdt@bookTicker
    return service
        .subscribeChannel(channelName, args)
        .map(node -> mapper.treeToValue(node, info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketBookTicker.class))
        .map(CoinsphStreamingAdapters::adaptBookTicker);
  }
}