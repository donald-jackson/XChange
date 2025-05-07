package org.knowm.xchange.coinsph;

import java.io.IOException;
import java.io.InputStream;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinsph.service.CoinsPHAccountService;
import org.knowm.xchange.coinsph.service.CoinsPHMarketDataService;
import org.knowm.xchange.coinsph.service.CoinsPHTradeService;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.utils.nonce.CurrentTimeIncrementalNonceFactory;
import si.mazi.rescu.SynchronizedValueFactory;

/** Exchange implementation for Coins.ph cryptocurrency exchange. */
public class CoinsPHExchange extends BaseExchange implements Exchange {

  private final SynchronizedValueFactory<Long> nonceFactory =
      new CurrentTimeIncrementalNonceFactory(java.util.concurrent.TimeUnit.MILLISECONDS);

  // Constants for URLs
  private static final String PRODUCTION_API_URL = "https://api.pro.coins.ph";
  private static final String SANDBOX_API_URL = "https://9001.pl-qa.coinsxyz.me";

  @Override
  protected void initServices() {
    this.marketDataService = new CoinsPHMarketDataService(this);
    this.accountService = new CoinsPHAccountService(this);
    this.tradeService = new CoinsPHTradeService(this);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification exchangeSpecification = new ExchangeSpecification(this.getClass());
    exchangeSpecification.setSslUri(PRODUCTION_API_URL);
    exchangeSpecification.setHost("api.pro.coins.ph");
    exchangeSpecification.setPort(443);
    exchangeSpecification.setExchangeName("Coins.ph");
    exchangeSpecification.setExchangeDescription(
        "Coins.ph is a cryptocurrency exchange based in the Philippines.");

    // Set parameters for sandbox mode
    exchangeSpecification.setExchangeSpecificParametersItem("use_sandbox", false);

    return exchangeSpecification;
  }

  /**
   * Get a specification for the sandbox environment.
   *
   * @return the sandbox exchange specification
   */
  public static ExchangeSpecification getSandboxExchangeSpecification() {
    ExchangeSpecification spec = new ExchangeSpecification(CoinsPHExchange.class);
    spec.setSslUri(SANDBOX_API_URL);
    spec.setHost("9001.pl-qa.coinsxyz.me");
    spec.setPort(443);
    spec.setExchangeName("Coins.ph Sandbox");
    spec.setExchangeDescription("Coins.ph Sandbox for testing API integration");
    spec.setExchangeSpecificParametersItem("use_sandbox", true);
    return spec;
  }

  @Override
  public void applySpecification(ExchangeSpecification exchangeSpecification) {
    super.applySpecification(exchangeSpecification);

    // Check if we should use sandbox URLs
    boolean useSandbox = false;
    if (exchangeSpecification.getExchangeSpecificParametersItem("use_sandbox") != null) {
      useSandbox =
          Boolean.TRUE.equals(
              exchangeSpecification.getExchangeSpecificParametersItem("use_sandbox"));
    }

    if (useSandbox) {
      exchangeSpecification.setSslUri(SANDBOX_API_URL);
      exchangeSpecification.setHost("9001.pl-qa.coinsxyz.me");
    }
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {
    return nonceFactory;
  }

  @Override
  public void remoteInit() throws IOException, ExchangeException {
    try {
      exchangeMetaData = loadExchangeMetaData();
    } catch (Exception e) {
      throw new ExchangeException("Failed to load exchange metadata", e);
    }
  }

  private org.knowm.xchange.dto.meta.ExchangeMetaData loadExchangeMetaData() {
    try (InputStream is =
        BaseExchangeService.class.getClassLoader().getResourceAsStream("coinsph.json")) {
      return loadMetaData(is, org.knowm.xchange.dto.meta.ExchangeMetaData.class);
    } catch (IOException e) {
      throw new ExchangeException(e);
    }
  }
}
