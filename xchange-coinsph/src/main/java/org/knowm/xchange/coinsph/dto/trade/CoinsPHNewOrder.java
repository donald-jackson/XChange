package org.knowm.xchange.coinsph.dto.trade;

import java.math.BigDecimal;

/**
 * New order request for Coins.ph
 */
public class CoinsPHNewOrder {

  private final String symbol;
  private final String side;
  private final String type;
  private final String timeInForce;
  private final BigDecimal quantity;
  private final BigDecimal quoteOrderQty;
  private final BigDecimal price;
  private final String newClientOrderId;
  private final BigDecimal stopPrice;
  private final String newOrderRespType;

  public CoinsPHNewOrder(
      String symbol,
      String side,
      String type,
      String timeInForce,
      BigDecimal quantity,
      BigDecimal quoteOrderQty,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      String newOrderRespType) {
    this.symbol = symbol;
    this.side = side;
    this.type = type;
    this.timeInForce = timeInForce;
    this.quantity = quantity;
    this.quoteOrderQty = quoteOrderQty;
    this.price = price;
    this.newClientOrderId = newClientOrderId;
    this.stopPrice = stopPrice;
    this.newOrderRespType = newOrderRespType;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getSide() {
    return side;
  }

  public String getType() {
    return type;
  }

  public String getTimeInForce() {
    return timeInForce;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public BigDecimal getQuoteOrderQty() {
    return quoteOrderQty;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getNewClientOrderId() {
    return newClientOrderId;
  }

  public BigDecimal getStopPrice() {
    return stopPrice;
  }

  public String getNewOrderRespType() {
    return newOrderRespType;
  }
}