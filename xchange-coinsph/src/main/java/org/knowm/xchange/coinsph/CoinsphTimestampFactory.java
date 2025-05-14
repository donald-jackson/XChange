package org.knowm.xchange.coinsph;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.knowm.xchange.client.ExchangeSettings;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.service.BaseParamsDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.SynchronizedValueFactory;

public class CoinsphTimestampFactory implements SynchronizedValueFactory<Long> {

  private static final Logger LOG = LoggerFactory.getLogger(CoinsphTimestampFactory.class);

  private final Coinsph coinsph;
  private final ExchangeSettings exchangeSettings;
  private final ResilienceRegistries resilienceRegistries;

  private Long deltaServerTime; // difference between server time and client time in milliseconds

  private CoinsphTimestampFactory(
      Coinsph coinsph, ExchangeSettings exchangeSettings, ResilienceRegistries resilienceRegistries) {
    this.coinsph = coinsph;
    this.exchangeSettings = exchangeSettings;
    this.resilienceRegistries = resilienceRegistries;
  }

  public static CoinsphTimestampFactory createFactory(
      Coinsph coinsph, ExchangeSettings exchangeSettings, ResilienceRegistries resilienceRegistries) {
    CoinsphTimestampFactory factory =
        new CoinsphTimestampFactory(coinsph, exchangeSettings, resilienceRegistries);
    if (exchangeSettings.isResilientClient()) {
      // Only try to sync time if resilient client is enabled
      factory.resync();
    }
    return factory;
  }

  @Override
  public Long createValue() {
    if (exchangeSettings.isResilientClient() && deltaServerTime != null) {
      return System.currentTimeMillis() + deltaServerTime;
    }
    return System.currentTimeMillis();
  }

  public void resync() {
    if (!exchangeSettings.isResilientClient()) {
      // Do not attempt to sync if resilience is disabled
      deltaServerTime = null; // Ensure it's reset if it was previously set
      return;
    }
    try {
      // Using a simplified resilience mechanism for this internal call
      // Or, if a specific retry/rateLimiter is defined for 'time' endpoint, use that.
      long serverTime = coinsph.time().getServerTime();
      long systemTime = System.currentTimeMillis();
      deltaServerTime = serverTime - systemTime;
      LOG.info("deltaServerTime: {} ms", deltaServerTime);
    } catch (IOException e) {
      LOG.warn("An error occurred while calling Coins.ph time server: {}", e.getMessage());
      LOG.warn("Using System.currentTimeMillis() as fallback for this request, but deltaServerTime will not be updated.");
      // Keep the old deltaServerTime if it exists, otherwise it remains null
    } catch (Exception e) {
      LOG.warn("An unexpected error occurred during time synchronization: {}", e.getMessage(), e);
      // Keep the old deltaServerTime
    }
  }

  /**
   * Returns the server time delta in milliseconds if it has been initialized, otherwise null.
   * Primarily for testing.
   */
  public Long getDeltaServerTime() {
    return deltaServerTime;
  }
}