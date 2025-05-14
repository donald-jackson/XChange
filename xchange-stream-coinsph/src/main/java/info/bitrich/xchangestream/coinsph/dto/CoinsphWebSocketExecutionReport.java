package info.bitrich.xchangestream.coinsph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CoinsphWebSocketExecutionReport {
  private final String eventType; // e
  private final long eventTime; // E
  private final String symbol; // s
  private final String clientOrderId; // c
  private final String side; // S
  private final String orderType; // o
  private final String timeInForce; // f
  private final BigDecimal orderQuantity; // q
  private final BigDecimal orderPrice; // p
  private final BigDecimal stopPrice; // P
  private final String executionType; // x
  private final String orderStatus; // X
  private final long orderId; // i
  private final BigDecimal lastExecutedQuantity; // l
  private final BigDecimal cumulativeFilledQuantity; // z
  private final BigDecimal lastExecutedPrice; // L
  private final BigDecimal commissionAmount; // n
  private final String commissionAsset; // N
  private final long transactionTime; // T
  private final long tradeId; // t
  private final boolean isOrderOnBook; // w (added)
  private final long orderCreationTime; // O
  private final String orderRejectReason; // r (added)
  private final BigDecimal cumulativeQuoteAssetTransactedQuantity; // Z (added)
  private final BigDecimal lastQuoteAssetTransactedQuantity; // Y (added)
  private final BigDecimal quoteOrderQuantity; // Q (added)


  public CoinsphWebSocketExecutionReport(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") long eventTime,
      @JsonProperty("s") String symbol,
      @JsonProperty("c") String clientOrderId,
      @JsonProperty("S") String side,
      @JsonProperty("o") String orderType,
      @JsonProperty("f") String timeInForce,
      @JsonProperty("q") BigDecimal orderQuantity,
      @JsonProperty("p") BigDecimal orderPrice,
      @JsonProperty("P") BigDecimal stopPrice,
      @JsonProperty("x") String executionType,
      @JsonProperty("X") String orderStatus,
      @JsonProperty("r") String orderRejectReason, // Added
      @JsonProperty("i") long orderId,
      @JsonProperty("l") BigDecimal lastExecutedQuantity,
      @JsonProperty("z") BigDecimal cumulativeFilledQuantity,
      @JsonProperty("L") BigDecimal lastExecutedPrice,
      @JsonProperty("n") BigDecimal commissionAmount,
      @JsonProperty("N") String commissionAsset,
      @JsonProperty("T") long transactionTime,
      @JsonProperty("t") long tradeId,
      @JsonProperty("w") boolean isOrderOnBook, // Added
      @JsonProperty("O") long orderCreationTime,
      @JsonProperty("Z") BigDecimal cumulativeQuoteAssetTransactedQuantity, // Added
      @JsonProperty("Y") BigDecimal lastQuoteAssetTransactedQuantity, // Added
      @JsonProperty("Q") BigDecimal quoteOrderQuantity // Added
      ) {
    this.eventType = eventType;
    this.eventTime = eventTime;
    this.symbol = symbol;
    this.clientOrderId = clientOrderId;
    this.side = side;
    this.orderType = orderType;
    this.timeInForce = timeInForce;
    this.orderQuantity = orderQuantity;
    this.orderPrice = orderPrice;
    this.stopPrice = stopPrice;
    this.executionType = executionType;
    this.orderStatus = orderStatus;
    this.orderRejectReason = orderRejectReason;
    this.orderId = orderId;
    this.lastExecutedQuantity = lastExecutedQuantity;
    this.cumulativeFilledQuantity = cumulativeFilledQuantity;
    this.lastExecutedPrice = lastExecutedPrice;
    this.commissionAmount = commissionAmount;
    this.commissionAsset = commissionAsset;
    this.transactionTime = transactionTime;
    this.tradeId = tradeId;
    this.isOrderOnBook = isOrderOnBook;
    this.orderCreationTime = orderCreationTime;
    this.cumulativeQuoteAssetTransactedQuantity = cumulativeQuoteAssetTransactedQuantity;
    this.lastQuoteAssetTransactedQuantity = lastQuoteAssetTransactedQuantity;
    this.quoteOrderQuantity = quoteOrderQuantity;
  }
}