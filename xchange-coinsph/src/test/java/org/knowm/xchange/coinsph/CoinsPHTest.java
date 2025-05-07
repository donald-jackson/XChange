package org.knowm.xchange.coinsph;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.junit.Test;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHExchangeInfo;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHServerTime;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHSymbolFilter;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHSymbolInfo;

public class CoinsPHTest {

  @Test
  public void testUnmarshallServerTime() throws IOException {
    // given
    InputStream is = CoinsPHTest.class.getResourceAsStream("/marketdata/server_time.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    CoinsPHServerTime serverTime = mapper.readValue(is, CoinsPHServerTime.class);

    // then
    assertThat(serverTime).isNotNull();
    assertThat(serverTime.getServerTime()).isGreaterThan(0);
  }

  @Test
  public void testUnmarshallExchangeInfo() throws IOException {
    // given
    InputStream is = CoinsPHTest.class.getResourceAsStream("/marketdata/exchange_info.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ExtendedExchangeInfo exchangeInfo = mapper.readValue(is, ExtendedExchangeInfo.class);

    // then
    assertThat(exchangeInfo).isNotNull();
    assertThat(exchangeInfo.getTimezone()).isNotNull();
    assertThat(exchangeInfo.getServerTime()).isGreaterThan(0);

    // Symbols
    assertThat(exchangeInfo.getSymbols()).isNotNull();
    if (!exchangeInfo.getSymbols().isEmpty()) {
      assertThat(exchangeInfo.getSymbols().get(0).getSymbol()).isNotNull();
      assertThat(exchangeInfo.getSymbols().get(0).getBaseAsset()).isNotNull();
      assertThat(exchangeInfo.getSymbols().get(0).getQuoteAsset()).isNotNull();
    }

    // Check additional properties
    assertThat(exchangeInfo.getAdditionalProperties()).isNotEmpty();
    assertThat(exchangeInfo.getAdditionalProperties().containsKey("exchangeFilters")).isTrue();
  }

  // Extended class with additionalProperties support
  @Getter
  public static class ExtendedExchangeInfo extends CoinsPHExchangeInfo {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

    @Override
    public void setSymbols(List<CoinsPHSymbolInfo> symbols) {
      List<CoinsPHSymbolInfo> extendedSymbols = new ArrayList<>();
      for (CoinsPHSymbolInfo symbol : symbols) {
        ExtendedSymbolInfo extSymbol = new ExtendedSymbolInfo();
        extSymbol.setSymbol(symbol.getSymbol());
        extSymbol.setStatus(symbol.getStatus());
        extSymbol.setBaseAsset(symbol.getBaseAsset());
        extSymbol.setBaseAssetPrecision(symbol.getBaseAssetPrecision());
        extSymbol.setQuoteAsset(symbol.getQuoteAsset());
        extSymbol.setQuoteAssetPrecision(symbol.getQuoteAssetPrecision());
        extSymbol.setOrderTypes(symbol.getOrderTypes());

        if (symbol.getFilters() != null) {
          List<CoinsPHSymbolFilter> extendedFilters = new ArrayList<>();
          for (CoinsPHSymbolFilter filter : symbol.getFilters()) {
            ExtendedSymbolFilter extFilter = getExtendedSymbolFilter(filter);
            extendedFilters.add(extFilter);
          }
          extSymbol.setFilters(extendedFilters);
        }

        extendedSymbols.add(extSymbol);
      }
      super.setSymbols(extendedSymbols);
    }

    private ExtendedSymbolFilter getExtendedSymbolFilter(CoinsPHSymbolFilter filter) {
      ExtendedSymbolFilter extFilter = new ExtendedSymbolFilter();
      extFilter.setFilterType(filter.getFilterType());
      extFilter.setMinPrice(filter.getMinPrice());
      extFilter.setMaxPrice(filter.getMaxPrice());
      extFilter.setTickSize(filter.getTickSize());
      extFilter.setMinQty(filter.getMinQty());
      extFilter.setMaxQty(filter.getMaxQty());
      extFilter.setStepSize(filter.getStepSize());
      extFilter.setMinNotional(filter.getMinNotional());
      extFilter.setMaxNumOrders(filter.getMaxNumOrders());
      extFilter.setMaxNumAlgoOrders(filter.getMaxNumAlgoOrders());
      return extFilter;
    }
  }

  @Getter
  public static class ExtendedSymbolInfo extends CoinsPHSymbolInfo {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }
  }

  @Getter
  public static class ExtendedSymbolFilter extends CoinsPHSymbolFilter {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }
  }
}
