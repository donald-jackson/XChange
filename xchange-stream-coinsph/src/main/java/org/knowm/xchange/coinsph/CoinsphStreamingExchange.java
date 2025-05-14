package org.knowm.xchange.coinsph;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.service.netty.NettyStreamingService; // Base class for streaming service
import io.reactivex.Completable;
import io.reactivex.Observable;
// TODO: Replace with Coinsph specific DTOs and Services as they are created
// import org.knowm.xchange.coinsph.dto.account.CoinsphStreamingAccountInformation;
import org.knowm.xchange.coinsph.service.CoinsphStreamingMarketDataService;
import org.knowm.xchange.coinsph.service.CoinsphStreamingAccountService;
import org.knowm.xchange.coinsph.service.CoinsphStreamingTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinsphStreamingExchange extends CoinsphExchange implements StreamingExchange {
  private static final Logger LOG = LoggerFactory.getLogger(CoinsphStreamingExchange.class);
  // Coins.ph WebSocket API URLs (Verify from documentation: https://docs.coins.ph/web-socket-streams/)
  // Public streams: wss://stream.coins.ph/ws/{symbol}@depth / {symbol}@trade etc.
  // User data streams: wss://stream.coins.ph/ws/{listenKey}
  // Sandbox URLs might be different, e.g., wss://9001.pl-qa.coinsxyz.me/ws (if sandbox supports websockets)
  // For now, let's assume a base URI and specific paths/listenKeys are handled by the streaming service.
  private static final String SANDBOX_WEBSOCKET_URI_BASE = "wss://9001.pl-qa.coinsxyz.me/openapi/v1/ws"; // From docs: User Data Stream
  private static final String PRODUCTION_WEBSOCKET_URI_BASE = "wss://stream.coins.ph/openapi/v1/ws"; // From docs: User Data Stream
  // Public streams might use a different base or path structure, e.g. wss://stream.coins.ph/ws

  private CoinsphStreamingService streamingService; // This will be our custom implementation
  private CoinsphStreamingMarketDataService streamingMarketDataService;
  private CoinsphStreamingTradeService streamingTradeService;
  private CoinsphStreamingAccountService streamingAccountService;

  public CoinsphStreamingExchange() {}

  protected String getStreamingBaseUri() {
    // This needs to be more nuanced. Public streams and User Data Streams might have different base URLs or connection mechanisms.
    // For User Data Streams, the listenKey is part of the path.
    // For public streams, it's often a common base URI.
    // Let's assume for now this is for the User Data Stream, as public streams are often unauthenticated.
    // The actual URI construction will happen in CoinsphStreamingService.
    return exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX).equals(true)
        ? SANDBOX_WEBSOCKET_URI_BASE
        : PRODUCTION_WEBSOCKET_URI_BASE;
  }

  @Override
  protected void initServices() {
    super.initServices(); // Initialize REST services

    // Initialize streaming services
    // The streamingService needs to be instantiated with the correct URI(s)
    // and potentially API keys for authenticated streams.
    this.streamingService = createStreamingService();
    this.streamingMarketDataService = new CoinsphStreamingMarketDataService(streamingService);
    this.streamingTradeService = new CoinsphStreamingTradeService(streamingService, getTradeService()); // Pass REST trade service for fallbacks or combined ops
    this.streamingAccountService = new CoinsphStreamingAccountService(streamingService, getAccountService());
  }

  protected CoinsphStreamingService createStreamingService() {
    // This is a placeholder. The actual CoinsphStreamingService will handle URI construction
    // and authentication (e.g., listen key for user data).
    String baseUri = getStreamingBaseUri(); // This might be just one of the URIs needed.
    LOG.info("Attempting to configure Coins.ph WebSocket service with base URI: {}", baseUri);
    // The constructor of CoinsphStreamingService will need more details.
    return new CoinsphStreamingService(baseUri, getStreamingConfiguration(), this);
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    // ProductSubscription might not be directly used if Coins.ph uses a listenKey model for user data
    // and specific channel names for public data.
    // The actual subscription logic will be in CoinsphStreamingService.
    if (streamingService == null) {
        streamingService = createStreamingService();
    }
    return streamingService.connect();
  }

  @Override
  public Completable disconnect() {
    if (streamingService != null) {
        return streamingService.disconnect();
    }
    return Completable.complete();
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public StreamingAccountService getStreamingAccountService() {
    return streamingAccountService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    // Coins.ph WebSocket docs don't explicitly mention compression. Assume false or check.
    // If supported, streamingService should handle it.
    if (streamingService != null) {
        streamingService.useCompressedMessages(compressedMessages);
    }
  }

  @Override
  public void resubscribeChannels() {
    LOG.debug("Resubscribing channels");
    if (streamingService != null) {
        streamingService.resubscribeChannels();
    }
  }
}