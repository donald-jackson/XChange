package org.knowm.xchange.coinsph;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.service.netty.NettyStreamingService; // Base class for streaming service
import io.reactivex.Completable;
import io.reactivex.Observable;
import info.bitrich.xchangestream.coinsph.CoinsphStreamingAccountService;
import info.bitrich.xchangestream.coinsph.CoinsphStreamingMarketDataService;
import info.bitrich.xchangestream.coinsph.CoinsphStreamingService;
import info.bitrich.xchangestream.coinsph.CoinsphStreamingTradeService;
// TODO: Replace with Coinsph specific DTOs and Services as they are created
// import org.knowm.xchange.coinsph.dto.account.CoinsphStreamingAccountInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinsphStreamingExchange extends CoinsphExchange implements StreamingExchange {
  private static final Logger LOG = LoggerFactory.getLogger(CoinsphStreamingExchange.class);

  // Public Market Streams Base URIs
  // (User Data Streams will be wss://stream.coins.ph/ws/{listenKey} or ws://192.168.8.157:9999/ws/{listenKey})
  // The actual listenKey is obtained via REST API: /openapi/v1/userDataStream
  private static final String SANDBOX_PUBLIC_STREAM_URI = "ws://192.168.8.157:9999/ws"; // Assumes sandbox proxy uses ws://
  private static final String PRODUCTION_PUBLIC_STREAM_URI = "wss://stream.coins.ph/ws";

  // User Data Stream URIs (base, listenKey will be appended)
  // These are the same as public stream URIs, but the path will include the listenKey.
  // CoinsphStreamingService will manage this.
  private static final String SANDBOX_USER_STREAM_BASE_URI = "ws://192.168.8.157:9999/ws/"; // Trailing slash for listenKey
  private static final String PRODUCTION_USER_STREAM_BASE_URI = "wss://stream.coins.ph/ws/"; // Trailing slash for listenKey

  // Service for public market data streams (e.g., order books, tickers, trades)
  private JsonNettyStreamingService publicStreamingService;
  private info.bitrich.xchangestream.coinsph.CoinsphStreamingMarketDataService streamingMarketDataService;

  // Service for private user data streams (e.g., account updates, user trades)
  // This is our custom service that handles listenKey internally.
  private info.bitrich.xchangestream.coinsph.CoinsphStreamingService userStreamingService;
  private info.bitrich.xchangestream.coinsph.CoinsphStreamingTradeService streamingTradeService;
  private info.bitrich.xchangestream.coinsph.CoinsphStreamingAccountService streamingAccountService;


  public CoinsphStreamingExchange() {}

  /**
   * Returns the base URI for public streams.
   */
  protected String getPublicStreamingBaseUri() {
    return Boolean.TRUE.equals(exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX))
        ? SANDBOX_PUBLIC_STREAM_URI
        : PRODUCTION_PUBLIC_STREAM_URI;
  }

  /**
   * Returns the base URI for user data streams (listenKey needs to be appended by CoinsphStreamingService).
   */
  protected String getUserStreamingBaseUri() {
    return Boolean.TRUE.equals(exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX))
        ? SANDBOX_USER_STREAM_BASE_URI
        : PRODUCTION_USER_STREAM_BASE_URI;
  }


  @Override
  protected void initServices() {
    super.initServices(); // Initialize REST services

    // Create and initialize streaming services
    // Public Market Data Service
    this.publicStreamingService = createPublicStreamingService();
    this.streamingMarketDataService = new CoinsphStreamingMarketDataService(this.publicStreamingService);

    // User Data Service (Account, Trades)
    // Only create if API keys are provided, as it requires authentication for listenKey
    if (exchangeSpecification.getApiKey() != null && exchangeSpecification.getSecretKey() != null) {
        try {
            this.userStreamingService = createUserStreamingService();
            this.streamingTradeService = new CoinsphStreamingTradeService(this.userStreamingService, getTradeService());
            this.streamingAccountService = new CoinsphStreamingAccountService(this.userStreamingService, getAccountService());
        } catch (Exception e) {
            LOG.warn("Failed to initialize User Data Streaming Service for Coins.ph: {}. Trade and Account streaming will be unavailable.", e.getMessage());
            // Allow exchange to function for public data even if private streams fail to init
        }
    } else {
        LOG.info("API key and/or secret key not provided. User Data Streaming Service for Coins.ph will not be initialized.");
    }
  }

  protected JsonNettyStreamingService createPublicStreamingService() {
    LOG.info("Creating Public Streaming Service for Coins.ph with URI: {}", getPublicStreamingBaseUri());
    // Using a basic JsonNettyStreamingService for public streams.
    // If Coins.ph public streams need custom handling (e.g. specific subscription messages not covered by default),
    // a dedicated CoinsphPublicStreamingService extending JsonNettyStreamingService might be needed.
    // For now, assume default behavior is sufficient for public channel subscriptions.
    JsonNettyStreamingService service = new JsonNettyStreamingService(getPublicStreamingBaseUri(), getStreamingConfiguration());
    // TODO: Set custom ObjectMapper if needed for public streams, e.g., via service.setStreamingObjectMapperHelper(...)
    return service;
  }

  protected info.bitrich.xchangestream.coinsph.CoinsphStreamingService createUserStreamingService() throws java.io.IOException {
    LOG.info("Creating User Data Streaming Service for Coins.ph.");
    // This constructor now handles listenKey acquisition and throws IOException on failure.
    return new info.bitrich.xchangestream.coinsph.CoinsphStreamingService(this, getStreamingConfiguration());
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    // Connect both public and private services if they exist.
    Completable publicConnect = Completable.complete();
    Completable userConnect = Completable.complete();

    if (publicStreamingService != null) {
        LOG.info("Connecting to Coins.ph public streams...");
        publicConnect = publicStreamingService.connect();
    }
    
    // The 'args' (ProductSubscription) are typically for public streams.
    // User streams are implicitly subscribed by connecting with a listenKey.
    // If args are passed, they should be applied to the publicStreamingService after connection.
    // This part might need refinement based on how ProductSubscription is used by clients.
    // For now, let's assume connect() handles initial connection, and subscriptions are separate.

    if (userStreamingService != null) {
        LOG.info("Connecting to Coins.ph user data stream...");
        userConnect = userStreamingService.connect();
    }

    return Completable.concatArray(publicConnect, userConnect)
        .doOnError(throwable -> LOG.error("Failed to connect to one or more Coins.ph streams", throwable))
        .doOnComplete(() -> LOG.info("Successfully connected to relevant Coins.ph streams."));
  }

  @Override
  public Completable disconnect() {
    Completable publicDisconnect = Completable.complete();
    Completable userDisconnect = Completable.complete();

    if (publicStreamingService != null) {
        LOG.info("Disconnecting from Coins.ph public streams...");
        publicDisconnect = publicStreamingService.disconnect();
    }
    if (userStreamingService != null) {
        LOG.info("Disconnecting from Coins.ph user data stream...");
        userDisconnect = userStreamingService.disconnect();
    }
    return Completable.concatArray(publicDisconnect, userDisconnect);
  }

  @Override
  public boolean isAlive() {
    // Consider alive if at least one relevant service is alive.
    // Or, if only public is used, check public. If user data is primary, check that.
    // For now, true if either (if initialized) is alive.
    boolean publicAlive = publicStreamingService != null && publicStreamingService.isSocketOpen();
    boolean userAlive = userStreamingService != null && userStreamingService.isSocketOpen();
    
    if (userStreamingService != null && publicStreamingService != null) return publicAlive || userAlive; // Or publicAlive && userAlive depending on requirements
    if (userStreamingService != null) return userAlive;
    if (publicStreamingService != null) return publicAlive;
    return false;
  }

  // Reconnect and ConnectionSuccess need to be aggregated or handled per service.
  // This simplistic approach might not be ideal for granular error/success reporting.
  // For now, forwarding from public service if available, else user service.
  @Override
  public Observable<Throwable> reconnectFailure() {
    if (publicStreamingService != null) return publicStreamingService.subscribeReconnectFailure();
    if (userStreamingService != null) return userStreamingService.subscribeReconnectFailure();
    return Observable.empty();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    if (publicStreamingService != null) return publicStreamingService.subscribeConnectionSuccess();
    if (userStreamingService != null) return userStreamingService.subscribeConnectionSuccess();
    return Observable.empty();
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    if (streamingMarketDataService == null && publicStreamingService != null) {
        // Lazy init if public service was created but this wrapper wasn't (e.g. if initServices was called again)
        this.streamingMarketDataService = new CoinsphStreamingMarketDataService(this.publicStreamingService);
    }
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
     if (streamingTradeService == null && userStreamingService != null) {
        this.streamingTradeService = new CoinsphStreamingTradeService(this.userStreamingService, getTradeService());
    }
    return streamingTradeService;
  }

  @Override
  public StreamingAccountService getStreamingAccountService() {
    if (streamingAccountService == null && userStreamingService != null) {
        this.streamingAccountService = new CoinsphStreamingAccountService(this.userStreamingService, getAccountService());
    }
    return streamingAccountService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    if (publicStreamingService != null) {
        publicStreamingService.useCompressedMessages(compressedMessages);
    }
    if (userStreamingService != null) {
        userStreamingService.useCompressedMessages(compressedMessages);
    }
  }

  @Override
  public void resubscribeChannels() {
    LOG.debug("Resubscribing channels for Coins.ph");
    if (publicStreamingService != null) {
        // Public streams might need explicit resubscription logic if using JsonNettyStreamingService directly
        // and it doesn't handle it automatically in its resubscribeChannels.
        // For now, assume JsonNettyStreamingService handles its subscriptions.
        // If a custom CoinsphPublicStreamingService wrapper was used, it would implement this.
        LOG.debug("Resubscribing public channels (if supported by underlying service)...");
        publicStreamingService.resubscribeChannels(); 
    }
    if (userStreamingService != null) {
        // User data streams are typically implicit with listenKey, but resubscribe might re-check connection
        // or re-verify listenKey.
        LOG.debug("Resubscribing user data stream (if supported by underlying service)...");
        userStreamingService.resubscribeChannels();
    }
  }
}