package org.knowm.xchange.coinsph.dto.marketdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.junit.Test;

public class CoinsPHTicker24hTest {

  @Test
  public void testUnmarshall() throws IOException {
    // given
    InputStream is =
        CoinsPHTicker24hTest.class.getResourceAsStream("/marketdata/BTCUSDT_ticker.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHTicker24h ticker = mapper.readValue(is, CoinsPHTicker24h.class);

    // then
    assertThat(ticker.getSymbol()).isEqualTo("BTCUSDT");
    assertThat(ticker.getPriceChange()).isEqualTo(new BigDecimal("-14.45"));
    assertThat(ticker.getPriceChangePercent()).isEqualTo(new BigDecimal("-0.0002"));
    assertThat(ticker.getLastPrice()).isEqualTo(new BigDecimal("82998.56"));
    assertThat(ticker.getVolume()).isEqualTo(new BigDecimal("0"));
  }

  @Test
  public void testAdditionalProperties() throws IOException {
    // given
    InputStream is =
        CoinsPHTicker24hTest.class.getResourceAsStream("/marketdata/BTCUSDT_ticker.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHTicker24h ticker = mapper.readValue(is, CoinsPHTicker24h.class);

    // then - check that additionalProperties is not null
    assertThat(ticker.getAdditionalProperties()).isNotNull();
  }

  @Test
  public void testUnmarshallMultiple() throws IOException {
    // given
    InputStream is = CoinsPHTicker24hTest.class.getResourceAsStream("/marketdata/all_tickers.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHTicker24h[] tickers = mapper.readValue(is, CoinsPHTicker24h[].class);

    // then
    assertThat(tickers).isNotEmpty();
    assertThat(tickers.length).isGreaterThan(0);

    // Check that we can access properties on the tickers
    for (CoinsPHTicker24h ticker : tickers) {
      assertThat(ticker.getSymbol()).isNotEmpty();
      assertThat(ticker.getLastPrice()).isNotNull();
      assertThat(ticker.getAdditionalProperties()).isNotNull();
    }
  }
}
