package org.knowm.xchange.coinsph.dto.marketdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.junit.Test;

public class CoinsPHTradeTest {

  @Test
  public void testUnmarshall() throws IOException {
    // given
    InputStream is = CoinsPHTradeTest.class.getResourceAsStream("/marketdata/BTCUSDT_trades.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHTrade[] trades = mapper.readValue(is, CoinsPHTrade[].class);

    // then
    assertThat(trades).isNotEmpty();
    CoinsPHTrade trade = trades[0];

    // Check first trade
    assertThat(trade.getId()).isEqualTo(1933346507835793412L);
    assertThat(trade.getPrice()).isEqualTo(new BigDecimal("83012.460000000000000000"));
    assertThat(trade.getQty()).isEqualTo(new BigDecimal("0.002600000000000000"));
    assertThat(trade.getQuoteQty()).isEqualTo(new BigDecimal("215.832396"));
    assertThat(trade.getTime()).isEqualTo(1745208863654L);
    assertThat(trade.isBuyerMaker()).isFalse();
  }
}
