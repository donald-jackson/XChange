# Coins.ph XChange Integration

This module implements the XChange API for the Coins.ph cryptocurrency exchange.

## Features

The integration supports both the production environment (`api.pro.coins.ph`) and the sandbox environment (`9001.pl-qa.coinsxyz.me`).

### Supported Features

- Market Data API (public endpoints)
  - Get ticker information for trading pairs
  - Get order book data
  - Get recent trades

- Account API (authenticated endpoints, requires IP whitelisting)
  - Get account information and balances
  - Get deposit addresses for currencies
  - Withdraw funds

- Trading API (authenticated endpoints, requires IP whitelisting)
  - Place, cancel, and query orders
  - Get order history
  - Get trade history

### Authentication

The authentication mechanism uses HMAC-SHA256 signatures as required by the Coins.ph API. The implementation follows these steps:

1. Construct the query parameters string (including timestamp and recvWindow)
2. Create an HMAC-SHA256 signature of the query parameters using the API secret
3. Attach the signature to the request as a query parameter
4. Include the API key in the `X-COINS-APIKEY` HTTP header

## Implementation Status

- ✅ Public API endpoints (market data) - fully implemented and tested
- ✅ Authenticated API endpoints - implementation complete and working with proper signature implementation
- ✅ Sandbox environment support - fully implemented and tested
- ✅ Production environment support - implementation complete

### Implementation Details

The implementation includes a custom signature creator for HMAC-SHA256 signatures that properly formats the parameters for the API request. The standard signature implementation provided by XChange's REST framework did not work properly with the Coins.ph API.

Key enhancements:
- A `CoinsPHSignatureCreator` class that explicitly handles signature creation
- Proper handling of string-based values in balances and limits
- Comprehensive error handling with appropriate DTO structures
- Support for both sandbox and production environments

### Limitations

While the API integration is fully functional, there are some limitations to be aware of:

1. The Coins.ph API requires IP whitelisting for authenticated endpoints. Without whitelisting, you'll receive a `-2017` error code.
2. The test coverage is primarily focused on public endpoints as authenticated testing requires account setup.
3. The API implementation follows the Coins.ph API specification, which may differ slightly from other exchanges in the XChange framework.

For debugging purposes, the implementation includes robust error reporting that captures API errors in the DTO responses.

## Usage

### Setting Up the Exchange

```java
// For sandbox environment
ExchangeSpecification sandboxSpec = CoinsPHExchange.getSandboxExchangeSpecification();
sandboxSpec.setApiKey("your-api-key");
sandboxSpec.setSecretKey("your-api-secret");
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(sandboxSpec);

// For production environment
ExchangeSpecification spec = new CoinsPHExchange().getDefaultExchangeSpecification();
spec.setApiKey("your-api-key");
spec.setSecretKey("your-api-secret");
Exchange exchange = ExchangeFactory.INSTANCE.createExchange(spec);
```

### Using Market Data Service

```java
MarketDataService marketDataService = exchange.getMarketDataService();

// Get ticker for BTC/USDT
Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USDT);

// Get order book for BTC/USDT
OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_USDT);

// Get recent trades for BTC/USDT
Trades trades = marketDataService.getTrades(CurrencyPair.BTC_USDT);
```

### Using Account Service

```java
AccountService accountService = exchange.getAccountService();

// Get account info (requires API key and whitelisted IP)
try {
    AccountInfo accountInfo = accountService.getAccountInfo();
    System.out.println(accountInfo);
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}

// Get deposit address for a currency
try {
    String depositAddress = accountService.requestDepositAddress(Currency.BTC);
    System.out.println("BTC deposit address: " + depositAddress);
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
```

### Using Trade Service

```java
TradeService tradeService = exchange.getTradeService();

// Place a limit order
LimitOrder limitOrder = new LimitOrder.Builder(OrderType.BID, CurrencyPair.BTC_USDT)
    .limitPrice(new BigDecimal("20000.00"))
    .originalAmount(new BigDecimal("0.001"))
    .build();

try {
    String orderId = tradeService.placeLimitOrder(limitOrder);
    System.out.println("Placed limit order with ID: " + orderId);
} catch (Exception e) {
    System.out.println("Error placing limit order: " + e.getMessage());
}

// Get open orders
try {
    OpenOrders openOrders = tradeService.getOpenOrders();
    System.out.println("Open orders: " + openOrders.getOpenOrders().size());
} catch (Exception e) {
    System.out.println("Error getting open orders: " + e.getMessage());
}
```

## Issues and Limitations

### IP Whitelisting

Coins.ph API requires IP addresses to be whitelisted for authenticated requests. When attempting to use authenticated endpoints without a whitelisted IP, you will receive an error with code `-2017` and message `"Request ip is not in the whitelist"`.

To whitelist your IP address:
1. Log in to your Coins.ph account
2. Navigate to API Management section
3. Add your server's IP address to the whitelist
4. Contact Coins.ph support if you encounter any issues

If you're testing the API, you may need to request special permissions for your sandbox environment.

### Error Handling

Common API errors you might encounter:
- `-2017`: Request IP is not in the whitelist
- `-1022`: Signature for this request is not valid
- `-1021`: Timestamp for this request is outside of the recvWindow
- `-1100`: Illegal parameter format

### Testing Authenticated Endpoints

For complete testing of the authenticated endpoints, you will need:

1. Valid API credentials
2. A whitelisted IP address
3. Sufficient balance in your test account

## Test Resources

The module includes test resources to validate the integration:

- `CoinsPHMarketDataServiceIntegrationTest` - Tests for market data retrieval
- `CoinsPHDemoTest` - Example usage of the API

## API Documentation

For more details about the Coins.ph API, refer to the official documentation:
[Coins.ph API Documentation](https://coins-docs.github.io/rest-api/)