package info.bitrich.xchangestream.coinsph;

import com.fasterxml.jackson.databind.JsonNode;
// import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketTransaction; // Removed for now
import info.bitrich.xchangestream.coinsph.dto.CoinsphWebSocketSubscriptionMessage; // Added
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import java.io.IOException;
import java.time.Duration;
import org.knowm.xchange.coinsph.service.CoinsphAccountServiceRaw; // Added
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors; // Added
import java.util.concurrent.ScheduledExecutorService; // Added
import java.util.concurrent.TimeUnit; // Added
import java.util.concurrent.atomic.AtomicBoolean; // Added

public class CoinsphStreamingService extends JsonNettyStreamingService {
  private static final Logger LOG = LoggerFactory.getLogger(CoinsphStreamingService.class);
  private static final String SUBSCRIBE = "SUBSCRIBE";
  private static final String UNSUBSCRIBE = "UNSUBSCRIBE";
  private static final Duration DEFAULT_PING_INTERVAL_SECONDS = Duration.ofSeconds(3 * 60); // Coins.ph recommends pinging every 3-5 minutes for WebSocket
  private static final Duration LISTEN_KEY_KEEP_ALIVE_INTERVAL = Duration.ofMinutes(30); // Keep-alive typically every 30-50 mins

  private final CoinsphAccountServiceRaw accountServiceRaw;
  private volatile String listenKey = null;
  private volatile long listenKeyCreateTime = 0;
  private final AtomicBoolean isUserDataStreamSubscribed = new AtomicBoolean(false);
  private ScheduledExecutorService listenKeyKeepAliveExecutor;
  private final boolean isPrivateService; // Added: flag for user data stream service


  // Constructor for public streams
  public CoinsphStreamingService(String apiUrl, CoinsphAccountServiceRaw accountServiceRaw) {
    this(apiUrl, accountServiceRaw, false);
  }

  // Constructor for private (user data) streams
  public CoinsphStreamingService(String apiUrl, CoinsphAccountServiceRaw accountServiceRaw, boolean isPrivateService) {
    super(apiUrl, Integer.MAX_VALUE);
    this.accountServiceRaw = accountServiceRaw;
    this.isPrivateService = isPrivateService;
    setPingPongInterval(DEFAULT_PING_INTERVAL_SECONDS);
  }

@Override
  public String getApiUrl() {
    if (isPrivateService) {
      if (listenKey == null) {
        // Attempt to start user data stream to get listenKey if not already available.
        // This might be called before connect() if subscribe is called first,
        // or during connect() itself.
        LOG.info("ListenKey is null for private service, attempting to fetch/refresh...");
        startUserDataStream(); // This will try to obtain a listenKey
      }
      if (listenKey != null) {
        return super.getApiUrl() + "/openapi/ws/" + listenKey;
      } else {
        LOG.error("Cannot construct private API URL: listenKey is null even after attempting to fetch.");
        // Fallback or throw? JsonNettyStreamingService expects a URL.
        // Throwing an exception might be better to signal a critical failure.
        throw new IllegalStateException("ListenKey is required for private service URL but could not be obtained.");
      }
    }
    return super.getApiUrl();
  }

  @Override
  public Completable connect() {
    if (isPrivateService && listenKey == null) {
      // Ensure listenKey is fetched before attempting to connect for private streams.
      // startUserDataStream() will obtain the listenKey.
      // The actual URL construction with listenKey is handled by getApiUrl().
      LOG.info("Private service connecting, ensuring listenKey is available.");
      startUserDataStream(); 
      if (listenKey == null) {
        return Completable.error(new IllegalStateException("Failed to obtain listenKey for private service connection."));
      }
    }
    return super.connect();
  }
  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    // Coins.ph streams are typically <symbol>@<streamName> or just <streamName> for user data
    // Example: {"stream":"btcusdt@depth","data":{...}}
    // Or for user data: {"e":"executionReport", ...} (channel might be implicit or fixed)
    
    if (message.has("stream")) {
      return message.get("stream").asText();
    } else if (message.has("e")) { // For user data streams like executionReport
        // User data streams might not have a "stream" field, channel is implicit (listenKey)
        // Or we can use the event type as part of the channel name for routing
        return message.get("e").asText(); // e.g. "executionReport"
    }
    // Fallback or error if channel cannot be determined
    LOG.warn("Cannot determine channel from message: {}", message.toString());
    // Returning the raw message might help debug, or a constant like "UNKNOWN_CHANNEL"
    // For JsonNettyStreamingService, the channel name is crucial for routing.
    // If a message can't be mapped to a channel, it might be dropped or cause errors.
    // Consider if specific error handling or a default channel is more appropriate.
    try {
        return StreamingObjectMapperHelper.getObjectMapper().writeValueAsString(message);
    } catch (IOException e) {
        LOG.error("Error parsing channel from message: " + message.toString(), e);
        // Or handle error appropriately, maybe disconnect
    }
    return "UNKNOWN_MESSAGE_FORMAT"; // Fallback if all else fails
  }

  @Override
  protected String getSubscribeMessage(String channelName, Object... args) throws IOException {
    if (isPrivateService) {
      // For user data streams connected via /ws/<listenKey>, no explicit SUBSCRIBE message is needed.
      // The connection itself implies subscription to all user data events.
      LOG.debug("Private service: No explicit SUBSCRIBE message for channel {}", channelName);
      return null; 
    }
    // For public streams, use the standard subscription message format.
    CoinsphWebSocketSubscriptionMessage subscribeMessage =
        new CoinsphWebSocketSubscriptionMessage(SUBSCRIBE, new String[] {channelName}, getTimestamp());
    return objectMapper.writeValueAsString(subscribeMessage);
  }

  @Override
  protected String getUnsubscribeMessage(String channelName, Object... args) throws IOException {
    if (isPrivateService) {
      // Cannot explicitly unsubscribe from individual events on a user data stream connection.
      // To stop user data, the WebSocket connection is closed, or the listenKey is deleted.
      LOG.debug("Private service: No explicit UNSUBSCRIBE message for channel {}", channelName);
      return null;
    }
    // For public streams, use the standard unsubscription message format.
    CoinsphWebSocketSubscriptionMessage unsubscribeMessage =
        new CoinsphWebSocketSubscriptionMessage(UNSUBSCRIBE, new String[] {channelName}, getTimestamp());
    return objectMapper.writeValueAsString(unsubscribeMessage);
  }

  // Helper to get a unique ID for subscription messages (required by Coins.ph)
  private long getTimestamp() {
      return System.currentTimeMillis();
  }
  
  
  @Override
  protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
    // No extensions needed by default for Coins.ph from docs
    return null; 
  }

  @Override
  protected void handleMessage(JsonNode message) {
    // This method routes messages to subscribers based on channel name
    // If the message is a combined stream, it might look like:
    // {"stream":"<streamName>","data":{...}}
    // If it's a direct user data message, it might be the payload itself.

    try {
      String channel = getChannelNameFromMessage(message);
      JsonNode dataNode = message;
      if (message.has("stream") && message.has("data")) {
        // For combined streams, actual payload is in "data" field
        dataNode = message.get("data");
      }
      
      // Now, 'dataNode' contains the actual DTO payload.
      // 'channel' is used by super.handleMessage to route to the correct observable.
      super.handleMessage(dataNode); // Pass the actual payload to subscribers
      
    } catch (IOException e) {
        LOG.error("Error parsing channel from message: " + message.toString(), e);
        // Or handle error appropriately, maybe disconnect
    }
private boolean isUserDataChannel(String channelName) {
    // Define what channel names correspond to user data streams
    // e.g., "executionReport", "balanceUpdate", or the listenKey itself if used as channel
    return "executionReport".equals(channelName) || 
           "outboundAccountPosition".equals(channelName) || 
           "balanceUpdate".equals(channelName); // "balanceUpdate" is another event type from docs
    // Add other user-specific channels if any
  }

  private synchronized void startUserDataStream() {
    if (accountServiceRaw == null) {
      LOG.warn("CoinsphAccountServiceRaw not available, cannot start user data stream.");
      return;
    }
    if (listenKey != null && (System.currentTimeMillis() - listenKeyCreateTime) < LISTEN_KEY_KEEP_ALIVE_INTERVAL.toMillis() * 1.8) { // 1.8 for some buffer
        LOG.info("Listen key {} is still valid, not creating a new one.", listenKey);
        return;
    }

    try {
      LOG.info("Creating new listen key for user data stream.");
      listenKey = accountServiceRaw.createCoinsphListenKey().getListenKey();
      listenKeyCreateTime = System.currentTimeMillis();
      LOG.info("Obtained listen key: {}", listenKey);

      // Schedule keep-alive task
      if (listenKeyKeepAliveExecutor != null) {
        listenKeyKeepAliveExecutor.shutdownNow();
      }
      listenKeyKeepAliveExecutor = Executors.newSingleThreadScheduledExecutor();
      listenKeyKeepAliveExecutor.scheduleAtFixedRate(
          this::keepAliveListenKeyTask,
          LISTEN_KEY_KEEP_ALIVE_INTERVAL.toMinutes(),
          LISTEN_KEY_KEEP_ALIVE_INTERVAL.toMinutes(),
          TimeUnit.MINUTES);
      
      // The actual subscription to the WebSocket path /ws/<listenKey> might be handled by
      // how the apiUrl is constructed or by a specific connect method.
      // For Binance-style, the listenKey becomes part of the WebSocket URL.
      // If Coins.ph uses a different mechanism (e.g. sending listenKey in a message), adapt here.
      // For now, assume the main connection (super.connect()) will use an updated URL if needed,
      // or that subscription messages handle this.
      // If the API URL needs to change, this is more complex.
      // Let's assume for now that the base `apiUrl` is for public streams, and user streams
      // are multiplexed over the same connection after a separate auth/listenKey process,
      // or the `apiUrl` itself needs to be dynamic (e.g. `getApiUrl()` method).

      // Mark that user data stream is now active (or attempted)
      isUserDataStreamSubscribed.set(true); 

    } catch (IOException e) {
      LOG.error("Failed to create or keep-alive listen key: {}", e.getMessage(), e);
      listenKey = null; // Invalidate key on error
    }
  }

  private void keepAliveListenKeyTask() {
    if (listenKey == null || accountServiceRaw == null) {
      LOG.warn("Listen key or accountServiceRaw is null, cannot keep alive.");
      if (listenKeyKeepAliveExecutor != null) {
        listenKeyKeepAliveExecutor.shutdown(); // Stop trying if key is gone
      }
      return;
    }
    try {
      LOG.info("Keeping alive listen key: {}", listenKey);
      accountServiceRaw.keepAliveCoinsphListenKey(listenKey);
      LOG.debug("Listen key keep-alive successful for {}", listenKey);
    } catch (IOException e) {
      LOG.error("Failed to keep-alive listen key {}: {}. Will try to get a new one on next subscription.", listenKey, e.getMessage(), e);
      // Invalidate the key, so it's refreshed on next user data subscription attempt
      listenKey = null; 
      listenKeyCreateTime = 0;
      if (listenKeyKeepAliveExecutor != null) {
        listenKeyKeepAliveExecutor.shutdown(); // Stop trying with the old key
      }
      // Optionally, try to restart the user data stream immediately
      // startUserDataStream(); 
    }
  }

  private void closeListenKey() {
    if (listenKey != null && accountServiceRaw != null) {
      try {
        LOG.info("Closing listen key: {}", listenKey);
        accountServiceRaw.closeCoinsphListenKey(listenKey);
      } catch (IOException e) {
        LOG.error("Failed to close listen key {}: {}", listenKey, e.getMessage(), e);
      } finally {
        listenKey = null;
        listenKeyCreateTime = 0;
        if (listenKeyKeepAliveExecutor != null) {
          listenKeyKeepAliveExecutor.shutdownNow();
          listenKeyKeepAliveExecutor = null;
        }
      }
    }
  }

  @Override
  public Observable<JsonNode> subscribeChannel(String channelName, Object... args) {
    if (isPrivateService) {
      // For private service (user data streams):
      // 1. Ensure listenKey is active (connect() and getApiUrl() handle this primarily).
      //    startUserDataStream() is called during connect/getApiUrl if listenKey is needed.
      // 2. User data streams are typically implicit with the listenKey connection.
      //    No explicit SUBSCRIBE message is sent for channels like "executionReport".
      //    The messages will arrive on the WebSocket connection if the event occurs.
      //    We just need to return an Observable that filters messages from the stream.
      if (!isSocketOpen() && !isConnecting()) {
        LOG.info("Private service socket not open, attempting to connect before subscribing to {}.", channelName);
        connect().blockingAwait(); // Ensure connection is attempted before proceeding
      }
      LOG.info("Subscribing to private channel {} (no explicit server-side subscription message sent).", channelName);
      return super.subscribeChannel(channelName, args); // Relies on getChannelNameFromMessage to route
    } else {
      // For public service: send standard SUBSCRIBE message
      LOG.info("Subscribing to public channel {}.", channelName);
      return super.subscribeChannel(channelName, args);
    }
  }

  @Override
  public void disconnect() {
    closeListenKey(); // Clean up listen key on disconnect
    super.disconnect();
  }

  }