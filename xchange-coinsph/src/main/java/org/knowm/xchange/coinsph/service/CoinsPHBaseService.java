package org.knowm.xchange.coinsph.service;

import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.coinsph.CoinsPH;
import org.knowm.xchange.coinsph.CoinsPHAuthenticated;
import org.knowm.xchange.coinsph.CoinsPHExchange;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;

/** Base service for Coins.ph API */
public class CoinsPHBaseService extends BaseExchangeService<CoinsPHExchange>
    implements BaseService {

  protected final CoinsPH coinsPH;
  protected final CoinsPHAuthenticated coinsPHAuthenticated;
  protected final CoinsPHSignatureCreator coinsPHSignatureCreator;
  protected final String apiKey;

  /**
   * Constructor
   *
   * @param exchange the exchange to use for data
   */
  public CoinsPHBaseService(CoinsPHExchange exchange) {
    super(exchange);
    this.coinsPH =
        ExchangeRestProxyBuilder.forInterface(CoinsPH.class, exchange.getExchangeSpecification())
            .build();
    this.coinsPHAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                CoinsPHAuthenticated.class, exchange.getExchangeSpecification())
            .build();
    this.apiKey = exchange.getExchangeSpecification().getApiKey();

    this.coinsPHSignatureCreator =
        CoinsPHSignatureCreator.createInstance(exchange.getExchangeSpecification().getSecretKey());
  }
}
