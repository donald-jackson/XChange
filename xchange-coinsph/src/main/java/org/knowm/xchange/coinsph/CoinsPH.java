package org.knowm.xchange.coinsph;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHOrderbook;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTicker24h;
import org.knowm.xchange.coinsph.dto.marketdata.CoinsPHTrade;

/** Coins.ph public API endpoints */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface CoinsPH {

  @GET
  @Path("openapi/quote/v1/ticker/24hr")
  CoinsPHTicker24h get24hrTicker(@QueryParam("symbol") String symbol) throws IOException;

  @GET
  @Path("openapi/quote/v1/ticker/24hr")
  List<CoinsPHTicker24h> getAll24hrTickers() throws IOException;

  @GET
  @Path("openapi/quote/v1/depth")
  CoinsPHOrderbook getOrderBook(
      @QueryParam("symbol") String symbol, @QueryParam("limit") Integer limit) throws IOException;

  @GET
  @Path("openapi/quote/v1/trades")
  List<CoinsPHTrade> getTrades(
      @QueryParam("symbol") String symbol, @QueryParam("limit") Integer limit) throws IOException;

  @GET
  @Path("openapi/v1/time")
  CoinsPHServerTime getServerTime() throws IOException;

  @GET
  @Path("openapi/v1/exchangeInfo")
  CoinsPHExchangeInfo getExchangeInfo() throws IOException;

  /** Server time response class */
  class CoinsPHServerTime {
    private long serverTime;

    public long getServerTime() {
      return serverTime;
    }

    public void setServerTime(long serverTime) {
      this.serverTime = serverTime;
    }
  }

  /** Exchange info response class */
  class CoinsPHExchangeInfo {
    private String timezone;
    private long serverTime;
    private List<CoinsPHSymbolInfo> symbols;

    public String getTimezone() {
      return timezone;
    }

    public void setTimezone(String timezone) {
      this.timezone = timezone;
    }

    public long getServerTime() {
      return serverTime;
    }

    public void setServerTime(long serverTime) {
      this.serverTime = serverTime;
    }

    public List<CoinsPHSymbolInfo> getSymbols() {
      return symbols;
    }

    public void setSymbols(List<CoinsPHSymbolInfo> symbols) {
      this.symbols = symbols;
    }
  }

  /** Symbol info class */
  class CoinsPHSymbolInfo {
    private String symbol;
    private String status;
    private String baseAsset;
    private int baseAssetPrecision;
    private String quoteAsset;
    private int quoteAssetPrecision;
    private List<String> orderTypes;
    private List<CoinsPHSymbolFilter> filters;

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getBaseAsset() {
      return baseAsset;
    }

    public void setBaseAsset(String baseAsset) {
      this.baseAsset = baseAsset;
    }

    public int getBaseAssetPrecision() {
      return baseAssetPrecision;
    }

    public void setBaseAssetPrecision(int baseAssetPrecision) {
      this.baseAssetPrecision = baseAssetPrecision;
    }

    public String getQuoteAsset() {
      return quoteAsset;
    }

    public void setQuoteAsset(String quoteAsset) {
      this.quoteAsset = quoteAsset;
    }

    public int getQuoteAssetPrecision() {
      return quoteAssetPrecision;
    }

    public void setQuoteAssetPrecision(int quoteAssetPrecision) {
      this.quoteAssetPrecision = quoteAssetPrecision;
    }

    public List<String> getOrderTypes() {
      return orderTypes;
    }

    public void setOrderTypes(List<String> orderTypes) {
      this.orderTypes = orderTypes;
    }

    public List<CoinsPHSymbolFilter> getFilters() {
      return filters;
    }

    public void setFilters(List<CoinsPHSymbolFilter> filters) {
      this.filters = filters;
    }
  }

  /** Symbol filter class */
  class CoinsPHSymbolFilter {
    private String filterType;
    private String minPrice;
    private String maxPrice;
    private String tickSize;
    private String minQty;
    private String maxQty;
    private String stepSize;
    private String minNotional;
    private Integer maxNumOrders;
    private Integer maxNumAlgoOrders;

    public String getFilterType() {
      return filterType;
    }

    public void setFilterType(String filterType) {
      this.filterType = filterType;
    }

    public String getMinPrice() {
      return minPrice;
    }

    public void setMinPrice(String minPrice) {
      this.minPrice = minPrice;
    }

    public String getMaxPrice() {
      return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
      this.maxPrice = maxPrice;
    }

    public String getTickSize() {
      return tickSize;
    }

    public void setTickSize(String tickSize) {
      this.tickSize = tickSize;
    }

    public String getMinQty() {
      return minQty;
    }

    public void setMinQty(String minQty) {
      this.minQty = minQty;
    }

    public String getMaxQty() {
      return maxQty;
    }

    public void setMaxQty(String maxQty) {
      this.maxQty = maxQty;
    }

    public String getStepSize() {
      return stepSize;
    }

    public void setStepSize(String stepSize) {
      this.stepSize = stepSize;
    }

    public String getMinNotional() {
      return minNotional;
    }

    public void setMinNotional(String minNotional) {
      this.minNotional = minNotional;
    }

    public Integer getMaxNumOrders() {
      return maxNumOrders;
    }

    public void setMaxNumOrders(Integer maxNumOrders) {
      this.maxNumOrders = maxNumOrders;
    }

    public Integer getMaxNumAlgoOrders() {
      return maxNumAlgoOrders;
    }

    public void setMaxNumAlgoOrders(Integer maxNumAlgoOrders) {
      this.maxNumAlgoOrders = maxNumAlgoOrders;
    }
  }
}
