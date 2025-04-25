# XChange Coins.ph

## Overview
This module implements the XChange API for the [Coins.ph Exchange](https://pro.coins.ph/). It provides access to the REST and WebSocket APIs for market data, account information, and trading.

## Features
- Market Data Service: Ticker, Order Book, Trades
- Account Service: Account Info, Deposit Address, Withdrawals
- Trade Service: Place/Cancel Orders, Order History, Trade History
- WebSocket Support: Market Data Streams, User Data Streams

## Authentication
To use authenticated endpoints, you need to provide your API key and secret:

```java
ExchangeSpecification exSpec = new ExchangeSpecification(CoinsPHExchange.class);
exSpec.setApiKey("your-api-key");
exSpec.setSecretKey("your-secret-key");
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
```

## Usage Examples

### Market Data
```java
// Get exchange instance
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(CoinsPHExchange.class);
MarketDataService marketDataService = exchange.getMarketDataService();

// Get ticker
Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_PHP);

// Get order book
OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_PHP);

// Get trades
Trades trades = marketDataService.getTrades(CurrencyPair.BTC_PHP);
```

### Account Data
```java
// Get authenticated exchange instance
ExchangeSpecification exSpec = new ExchangeSpecification(CoinsPHExchange.class);
exSpec.setApiKey("your-api-key");
exSpec.setSecretKey("your-secret-key");
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
AccountService accountService = exchange.getAccountService();

// Get account info
AccountInfo accountInfo = accountService.getAccountInfo();

// Get deposit address
String depositAddress = accountService.requestDepositAddress(Currency.BTC);

// Withdraw funds
String withdrawalId = accountService.withdrawFunds(Currency.BTC, new BigDecimal("0.1"), "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
```

### Trading
```java
// Get authenticated exchange instance
ExchangeSpecification exSpec = new ExchangeSpecification(CoinsPHExchange.class);
exSpec.setApiKey("your-api-key");
exSpec.setSecretKey("your-secret-key");
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
TradeService tradeService = exchange.getTradeService();

// Place limit order
LimitOrder limitOrder = new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_PHP)
    .limitPrice(new BigDecimal("500000"))
    .originalAmount(new BigDecimal("0.01"))
    .build();
String orderId = tradeService.placeLimitOrder(limitOrder);

// Place market order
MarketOrder marketOrder = new MarketOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_PHP)
    .originalAmount(new BigDecimal("0.01"))
    .build();
String marketOrderId = tradeService.placeMarketOrder(marketOrder);

// Get open orders
OpenOrders openOrders = tradeService.getOpenOrders();

// Cancel order
boolean cancelled = tradeService.cancelOrder(orderId);
```

## Limitations
- Some endpoints may have rate limits imposed by Coins.ph
- WebSocket streaming requires additional setup with the xchange-stream-coinsph module

## References
- [Coins.ph API Documentation](https://coins-docs.github.io/rest-api/)
- [XChange Documentation](https://knowm.org/open-source/xchange/)