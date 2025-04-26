#!/bin/bash

# Create directories if they don't exist
mkdir -p src/test/resources/marketdata

# Base URL for the Coins.ph API
API_BASE_URL="https://api.pro.coins.ph"

# Define symbols to fetch data for
SYMBOLS=("BTCUSDT" "ETHUSDT" "DOGEUSDT")

# Function to fetch and save data
fetch_and_save() {
  local endpoint=$1
  local filename=$2
  local params=$3
  
  echo "Fetching $endpoint with params $params"
  curl -s "$API_BASE_URL$endpoint$params" > "src/test/resources/marketdata/$filename"
  
  # Check if the file contains valid JSON
  if jq empty "src/test/resources/marketdata/$filename" 2>/dev/null; then
    echo "✅ Successfully saved $filename"
  else
    echo "❌ Failed to fetch valid JSON for $filename"
    # Save a sample structure if the API call fails
    echo "{\"error\": \"API call failed or returned invalid JSON\"}" > "src/test/resources/marketdata/$filename"
  fi
}

# Fetch exchange info
fetch_and_save "/openapi/v1/exchangeInfo" "exchange_info.json" ""

# Fetch server time
fetch_and_save "/openapi/v1/time" "server_time.json" ""

# Fetch data for each symbol
for SYMBOL in "${SYMBOLS[@]}"; do
  # Fetch 24hr ticker
  fetch_and_save "/openapi/quote/v1/ticker/24hr" "${SYMBOL}_ticker.json" "?symbol=${SYMBOL}"
  
  # Fetch order book
  fetch_and_save "/openapi/quote/v1/depth" "${SYMBOL}_orderbook.json" "?symbol=${SYMBOL}&limit=100"
  
  # Fetch recent trades
  fetch_and_save "/openapi/quote/v1/trades" "${SYMBOL}_trades.json" "?symbol=${SYMBOL}&limit=50"
done

# Fetch all 24hr tickers
fetch_and_save "/openapi/quote/v1/ticker/24hr" "all_tickers.json" ""

echo "Finished fetching API data." 