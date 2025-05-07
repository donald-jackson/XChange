package org.knowm.xchange.coinsph.dto.marketdata;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.junit.Test;

/** Test CoinsPHOrderbook JSON parsing */
public class CoinsPHOrderbookTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testUnmarshall() throws IOException {
    // Read sample data from file
    InputStream is =
        CoinsPHOrderbookTest.class.getResourceAsStream("/marketdata/example-depth-data.json");
    CoinsPHOrderbook orderbook = mapper.readValue(is, CoinsPHOrderbook.class);

    // Test values);
    assertEquals(1988681136L, orderbook.getLastUpdateId());
    assertEquals(10, orderbook.getBids().size());
    assertEquals(10, orderbook.getAsks().size());
    assertEquals(new BigDecimal("82997.810000000000000000"), orderbook.getBids().get(0).get(0));
    assertEquals(new BigDecimal("0.004900000000000000"), orderbook.getBids().get(0).get(1));
    assertEquals(new BigDecimal("83013.010000000000000000"), orderbook.getAsks().get(0).get(0));
    assertEquals(new BigDecimal("0.026105300000000000"), orderbook.getAsks().get(0).get(1));
  }
}
