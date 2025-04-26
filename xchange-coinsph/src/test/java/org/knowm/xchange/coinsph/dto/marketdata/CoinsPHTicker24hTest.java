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
    assertThat(ticker.getPriceChange()).isEqualTo(new BigDecimal("1471.85"));
    assertThat(ticker.getPriceChangePercent()).isEqualTo(new BigDecimal("0.0158"));
    assertThat(ticker.getWeightedAvgPrice()).isEqualTo(new BigDecimal("94296.706994774392694251"));
    assertThat(ticker.getPrevClosePrice()).isEqualTo(new BigDecimal("93498.53"));
    assertThat(ticker.getLastPrice()).isEqualTo(new BigDecimal("94897.5"));
    assertThat(ticker.getLastQty()).isEqualTo(new BigDecimal("0.0000137"));
    assertThat(ticker.getBidPrice()).isEqualTo(new BigDecimal("94321.08"));
    assertThat(ticker.getBidQty()).isEqualTo(new BigDecimal("0.0003374"));
    assertThat(ticker.getAskPrice()).isEqualTo(new BigDecimal("95155.53"));
    assertThat(ticker.getAskQty()).isEqualTo(new BigDecimal("0.3032781"));
    assertThat(ticker.getOpenPrice()).isEqualTo(new BigDecimal("93425.65"));
    assertThat(ticker.getHighPrice()).isEqualTo(new BigDecimal("95506.75"));
    assertThat(ticker.getLowPrice()).isEqualTo(new BigDecimal("92715.96"));
    assertThat(ticker.getVolume()).isEqualTo(new BigDecimal("0.1366157"));
    assertThat(ticker.getQuoteVolume()).isEqualTo(new BigDecimal("12882.41"));
    assertThat(ticker.getOpenTime()).isEqualTo(1745537340000L);
    assertThat(ticker.getCloseTime()).isEqualTo(1745623769235L);
    assertThat(ticker.getFirstId()).isEqualTo(1936102212145734145L);
    assertThat(ticker.getLastId()).isEqualTo(1936826988111815175L);
    assertThat(ticker.getCount()).isEqualTo(708);

    // Check additionalProperties - should be empty since all fields are defined in the class
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
