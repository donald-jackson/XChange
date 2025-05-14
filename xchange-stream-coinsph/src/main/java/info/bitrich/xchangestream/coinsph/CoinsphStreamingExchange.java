package info.bitrich.xchangestream.coinsph;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.service.netty.StreamingService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.knowm.xchange.coinsph.CoinsphExchange;
import org.knowm.xchange.coinsph.service.CoinsphAccountServiceRaw;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class CoinsphStreamingExchange extends CoinsphExchange implements StreamingExchange {

  // TODO: Get actual WebSocket API URLs (public and private if different) from Coins.ph docs
  // Public/Combined stream URL from docs: wss://wsapi.pro.coins.ph/openapi/quote/stream
  private static final String PUBLIC_API_URI = "wss://wsapi.pro.coins.ph/openapi/quote/stream";
  // User data stream base URL from docs: wss://wsapi.pro.coins.ph
  // Full path is /openapi/ws/<listenKey>
  private static final String USER_DATA_API_BASE_URI = "wss://wsapi.pro.coins.ph";

  private CoinsphStreamingService publicStreamingService;
  private CoinsphStreamingService privateStreamingService; // For user data streams

  private CoinsphStreamingMarketDataService streamingMarketDataService;
  private CoinsphStreamingTradeService streamingTradeService;
  private CoinsphStreamingAccountService streamingAccountService;

  public CoinsphStreamingExchange() {}

  @Override
  protected void initServices() {
    super.initServices(); // Initializes REST services
  }

  private void initStreamingServices() {
    CoinsphAccountServiceRaw accountServiceRaw = (CoinsphAccountServiceRaw) getAccountService();

    this.publicStreamingService = new CoinsphStreamingService(PUBLIC_API_URI, null); // No account service needed for public
    this.privateStreamingService = new CoinsphStreamingService(USER_DATA_API_BASE_URI, accountServiceRaw, true); // Mark as private service

    this.streamingMarketDataService = new CoinsphStreamingMarketDataService(publicStreamingService);
    this.streamingTradeService = new CoinsphStreamingTradeService(privateStreamingService);
    this.streamingAccountService = new CoinsphStreamingAccountService(privateStreamingService);
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    if (publicStreamingService == null || privateStreamingService == null) {
      initStreamingServices();
    }
    // Connect public streams
    Completable publicConnect = publicStreamingService.connect().doOnComplete(() -> {
        // Handle product subscriptions for public streams if any
        // e.g., publicStreamingService.subscribeProducts(args);
    });
    // Connect private streams (this will trigger listenKey acquisition and dynamic URL)
    Completable privateConnect = privateStreamingService.connect(); 

    return Completable.concatArray(publicConnect, privateConnect);
  }

  @Override
  public Completable disconnect() {
    Completable publicDisconnect = publicStreamingService != null ? publicStreamingService.disconnect() : Completable.complete();
    Completable privateDisconnect = privateStreamingService != null ? privateStreamingService.disconnect() : Completable.complete();
    return Completable.concatArray(publicDisconnect, privateDisconnect);
  }

  @Override
  public boolean isAlive() {
    // Consider alive if both services are configured and at least one is open,
    // or define more specific logic (e.g. public must be alive for market data)
    boolean publicAlive = publicStreamingService != null && publicStreamingService.isSocketOpen();
    boolean privateAlive = privateStreamingService != null && privateStreamingService.isSocketOpen();
    return publicAlive || privateAlive; // Or publicAlive && privateAlive if both are essential
  }

  // These might need to be more nuanced if we want to distinguish between public/private service events
  @Override
  public Observable<Throwable> reconnectFailure() {
    // Merge or choose one? For now, let's take public as primary for general health.
    return publicStreamingService != null ? publicStreamingService.subscribeReconnectFailure() : Observable.empty();
    // Or: return Observable.merge(publicStreamingService.subscribeReconnectFailure(), privateStreamingService.subscribeReconnectFailure());
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return publicStreamingService != null ? publicStreamingService.subscribeConnectionSuccess() : Observable.empty();
    // Or: return Observable.merge(publicStreamingService.subscribeConnectionSuccess(), privateStreamingService.subscribeConnectionSuccess());
  }
  
  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    if (streamingMarketDataService == null) initStreamingServices();
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    if (streamingTradeService == null) initStreamingServices();
    return streamingTradeService;
  }

  @Override
  public info.bitrich.xchangestream.core.StreamingAccountService getStreamingAccountService() {
    if (streamingAccountService == null) initStreamingServices();
    return streamingAccountService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    if (compressedMessages) {
        LOG.warn("Compressed messages requested, but Coins.ph WebSocket compression support is unconfirmed. Ignoring.");
    }
    // If supported, apply to both services:
    // if (publicStreamingService != null) publicStreamingService.useCompressedMessages(compressedMessages);
    // if (privateStreamingService != null) privateStreamingService.useCompressedMessages(compressedMessages);
  }
}