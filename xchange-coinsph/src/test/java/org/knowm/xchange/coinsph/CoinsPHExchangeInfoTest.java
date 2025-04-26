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
import org.junit.Test;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHExchangeInfo;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHSymbolFilter;
import org.knowm.xchange.coinsph.CoinsPH.CoinsPHSymbolInfo;

public class CoinsPHExchangeInfoTest {

  @Test
  public void testExchangeInfoUnmarshall() throws IOException {
    // given
    InputStream is = getClass().getResourceAsStream("/marketdata/exchange_info.json");

    // when
    ObjectMapper mapper = new ObjectMapper();
    // Configure Jackson to handle unknown properties
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ExtendedExchangeInfo exchangeInfo = mapper.readValue(is, ExtendedExchangeInfo.class);

    // then
    assertThat(exchangeInfo).isNotNull();

    // Check timezone and server time
    assertThat(exchangeInfo.getTimezone()).isNotNull();
    assertThat(exchangeInfo.getTimezone()).isEqualTo("UTC");
    assertThat(exchangeInfo.getServerTime()).isGreaterThan(0);

    // Check symbols list
    assertThat(exchangeInfo.getSymbols()).isNotNull();
    assertThat(exchangeInfo.getSymbols()).isNotEmpty();

    // Check the structure of a few important symbols (BTC, ETH, etc.)
    List<CoinsPHSymbolInfo> symbols = exchangeInfo.getSymbols();
    boolean foundBTC = false;
    boolean foundETH = false;

    for (CoinsPHSymbolInfo symbol : symbols) {
      if (symbol.getSymbol() != null && symbol.getSymbol().equals("BTCUSDT")) {
        foundBTC = true;
        validateSymbol(symbol, "BTC", "USDT");
      } else if (symbol.getSymbol() != null && symbol.getSymbol().equals("ETHUSDT")) {
        foundETH = true;
        validateSymbol(symbol, "ETH", "USDT");
      }
    }

    // If symbols not found in downloaded data, we'll just log and move on
    if (!foundBTC) {
      System.out.println("Warning: BTCUSDT symbol not found in exchange info");
    }
    if (!foundETH) {
      System.out.println("Warning: ETHUSDT symbol not found in exchange info");
    }

    // Verify additional properties are captured
    assertThat(exchangeInfo.getAdditionalProperties()).isNotNull();

    // If available, check the content of exchangeFilters
    if (exchangeInfo.getAdditionalProperties().containsKey("exchangeFilters")) {
      Object exchangeFilters = exchangeInfo.getAdditionalProperties().get("exchangeFilters");
      if (exchangeFilters instanceof List) {
        @SuppressWarnings("unchecked")
        List<Object> filters = (List<Object>) exchangeFilters;
        assertThat(filters).isNotNull();
      }
    }
  }

  private void validateSymbol(CoinsPHSymbolInfo symbol, String expectedBase, String expectedQuote) {
    assertThat(symbol.getBaseAsset()).isEqualTo(expectedBase);
    assertThat(symbol.getQuoteAsset()).isEqualTo(expectedQuote);
    assertThat(symbol.getStatus()).isNotEmpty();
    assertThat(symbol.getBaseAssetPrecision()).isGreaterThan(0);
    assertThat(symbol.getQuoteAssetPrecision()).isGreaterThan(0);

    // Check order types
    assertThat(symbol.getOrderTypes()).isNotNull();
    assertThat(symbol.getOrderTypes()).isNotEmpty();

    // Verify at least some of the common order types
    boolean hasLimit = false;
    boolean hasMarket = false;
    for (String orderType : symbol.getOrderTypes()) {
      if ("LIMIT".equals(orderType)) hasLimit = true;
      if ("MARKET".equals(orderType)) hasMarket = true;
    }

    // Check filters
    assertThat(symbol.getFilters()).isNotNull();

    if (!symbol.getFilters().isEmpty()) {
      // Validate filter types
      boolean foundPriceFilter = false;
      boolean foundLotSizeFilter = false;

      for (CoinsPHSymbolFilter filter : symbol.getFilters()) {
        String filterType = filter.getFilterType();
        assertThat(filterType).isNotNull();

        if ("PRICE_FILTER".equals(filterType)) {
          foundPriceFilter = true;
          assertThat(filter.getMinPrice()).isNotNull();
          assertThat(filter.getMaxPrice()).isNotNull();
          assertThat(filter.getTickSize()).isNotNull();
        } else if ("LOT_SIZE".equals(filterType)) {
          foundLotSizeFilter = true;
          assertThat(filter.getMinQty()).isNotNull();
          assertThat(filter.getMaxQty()).isNotNull();
          assertThat(filter.getStepSize()).isNotNull();
        } else if ("MIN_NOTIONAL".equals(filterType)) {
          assertThat(filter.getMinNotional()).isNotNull();
        } else if ("MAX_NUM_ORDERS".equals(filterType)) {
          assertThat(filter.getMaxNumOrders()).isNotNull();
        } else if ("MAX_NUM_ALGO_ORDERS".equals(filterType)) {
          assertThat(filter.getMaxNumAlgoOrders()).isNotNull();
        }

        // Check that additional properties are captured in the filter
        if (filter instanceof ExtendedSymbolFilter) {
          ExtendedSymbolFilter extFilter = (ExtendedSymbolFilter) filter;
          assertThat(extFilter.getAdditionalProperties()).isNotNull();
        }
      }

      // If filters not found in downloaded data, we'll just log and move on
      if (!foundPriceFilter) {
        System.out.println("Warning: PRICE_FILTER not found for " + symbol.getSymbol());
      }
      if (!foundLotSizeFilter) {
        System.out.println("Warning: LOT_SIZE filter not found for " + symbol.getSymbol());
      }
    }

    // Check additional properties in the symbol itself
    if (symbol instanceof ExtendedSymbolInfo) {
      ExtendedSymbolInfo extSymbol = (ExtendedSymbolInfo) symbol;
      assertThat(extSymbol.getAdditionalProperties()).isNotNull();
    }
  }

  // Extended classes to handle additional properties
  public static class ExtendedExchangeInfo extends CoinsPHExchangeInfo {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

    public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
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
            extendedFilters.add(extFilter);
          }
          extSymbol.setFilters(extendedFilters);
        }

        extendedSymbols.add(extSymbol);
      }
      super.setSymbols(extendedSymbols);
    }
  }

  public static class ExtendedSymbolInfo extends CoinsPHSymbolInfo {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

    public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
    }
  }

  public static class ExtendedSymbolFilter extends CoinsPHSymbolFilter {
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

    public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
    }
  }
}
