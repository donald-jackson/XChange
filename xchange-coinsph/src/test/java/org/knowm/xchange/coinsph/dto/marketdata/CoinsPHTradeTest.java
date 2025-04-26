package org.knowm.xchange.coinsph.dto.marketdata;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;

public class CoinsPHTradeTest {

  @Test
  public void testUnmarshall() throws IOException {
    // given
    InputStream is = CoinsPHTradeTest.class.getResourceAsStream("/marketdata/BTCUSDT_trades.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    List<CoinsPHTrade> trades = mapper.readValue(is, new TypeReference<List<CoinsPHTrade>>() {});

    // then
    assertThat(trades).isNotEmpty();
    assertThat(trades.size()).isGreaterThan(0);

    // Check first trade
    CoinsPHTrade firstTrade = trades.get(0);
    assertThat(firstTrade.getId()).isEqualTo(1936767596540682756L);
    assertThat(firstTrade.getPrice()).isEqualTo(new BigDecimal("94300.110000000000000000"));
    assertThat(firstTrade.getQty()).isEqualTo(new BigDecimal("0.000028500000000000"));
    assertThat(firstTrade.getQuoteQty()).isEqualTo(new BigDecimal("2.687553135"));
    assertThat(firstTrade.getTime()).isEqualTo(1745616689208L);
    assertThat(firstTrade.isBuyerMaker()).isEqualTo(true);
    assertThat(firstTrade.isBestMatch()).isEqualTo(true);

    // Check additionalProperties - should be empty since all fields are defined in the class
    assertThat(firstTrade.getAdditionalProperties()).isNotNull();

    // Check second trade
    if (trades.size() > 1) {
      CoinsPHTrade secondTrade = trades.get(1);
      assertThat(secondTrade.getId()).isEqualTo(1936769609865323013L);
      assertThat(secondTrade.getPrice()).isEqualTo(new BigDecimal("94578.680000000000000000"));
      assertThat(secondTrade.getQty()).isEqualTo(new BigDecimal("0.000022600000000000"));
      assertThat(secondTrade.getQuoteQty()).isEqualTo(new BigDecimal("2.137478168"));
      assertThat(secondTrade.getTime()).isEqualTo(1745616929215L);
      assertThat(secondTrade.getAdditionalProperties()).isNotNull();
    }
  }
}
