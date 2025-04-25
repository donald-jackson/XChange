package org.knowm.xchange.coinsph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinsph.service.CoinsPHAccountService;
import org.knowm.xchange.coinsph.service.CoinsPHMarketDataService;
import org.knowm.xchange.coinsph.service.CoinsPHTradeService;

/**
 * Tests for the CoinsPHExchange class
 */
public class CoinsPHExchangeTest {

  @Test
  public void testCreateExchange() {
    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(CoinsPHExchange.class);
    assertThat(exchange).isNotNull();
    assertThat(exchange.getExchangeSpecification().getExchangeName()).isEqualTo("Coins.ph");
    assertThat(exchange.getExchangeSpecification().getSslUri()).isEqualTo("https://api.pro.coins.ph");
    assertThat(exchange.getExchangeSpecification().getHost()).isEqualTo("api.pro.coins.ph");
    assertThat(exchange.getExchangeSpecification().getPort()).isEqualTo(443);
  }

  @Test
  public void testCreateExchangeWithApiKeys() {
    ExchangeSpecification exSpec = new ExchangeSpecification(CoinsPHExchange.class);
    exSpec.setApiKey("testApiKey");
    exSpec.setSecretKey("testSecretKey");
    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
    
    assertThat(exchange).isNotNull();
    assertThat(exchange.getExchangeSpecification().getApiKey()).isEqualTo("testApiKey");
    assertThat(exchange.getExchangeSpecification().getSecretKey()).isEqualTo("testSecretKey");
  }

  @Test
  public void testServices() {
    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(CoinsPHExchange.class);
    
    assertThat(exchange.getMarketDataService()).isInstanceOf(CoinsPHMarketDataService.class);
    assertThat(exchange.getAccountService()).isInstanceOf(CoinsPHAccountService.class);
    assertThat(exchange.getTradeService()).isInstanceOf(CoinsPHTradeService.class);
  }
}