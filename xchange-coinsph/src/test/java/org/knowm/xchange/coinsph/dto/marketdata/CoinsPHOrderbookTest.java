package org.knowm.xchange.coinsph.dto.marketdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.junit.Test;

public class CoinsPHOrderbookTest {

  @Test
  public void testUnmarshall() throws IOException {
    // given
    InputStream is =
        CoinsPHOrderbookTest.class.getResourceAsStream("/marketdata/BTCUSDT_orderbook.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHOrderbook orderbook = mapper.readValue(is, CoinsPHOrderbook.class);

    // then
    assertThat(orderbook.getLastUpdateId()).isEqualTo(83693598114L);

    // Check bids
    assertThat(orderbook.getBids()).isNotEmpty();
    assertThat(orderbook.getBids().size()).isGreaterThan(0);

    // Check first bid
    assertThat(orderbook.getBids().get(0).size()).isEqualTo(2);
    assertThat(orderbook.getBids().get(0).get(0))
        .isEqualTo(new BigDecimal("94321.090000000000000000"));
    assertThat(orderbook.getBids().get(0).get(1)).isEqualTo(new BigDecimal("0.011031100000000000"));

    // Check asks
    assertThat(orderbook.getAsks()).isNotEmpty();
    assertThat(orderbook.getAsks().size()).isGreaterThan(0);

    // Check first ask
    assertThat(orderbook.getAsks().get(0).size()).isEqualTo(2);
    assertThat(orderbook.getAsks().get(0).get(0))
        .isEqualTo(new BigDecimal("95155.520000000000000000"));
    assertThat(orderbook.getAsks().get(0).get(1)).isEqualTo(new BigDecimal("0.026395500000000000"));

    // Check additionalProperties - should be empty since all fields are defined in the class
    assertThat(orderbook.getAdditionalProperties()).isNotNull();
  }
}
