package org.knowm.xchange.fix.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.marketdata.MarketDataService;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataRequest;

public class FixMarketDataService extends FixBaseService implements MarketDataService {

    public FixMarketDataService(Exchange exchange) {
        super(exchange);
    }

    @Override
    public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {
        CompletableFuture<Ticker> future = new CompletableFuture<>();
        
        MarketDataRequest request = new MarketDataRequest();
        request.set(new MDReqID("TICKER_" + currencyPair.toString()));
        request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        request.set(new MarketDepth(1));
        request.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        
        MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
        entryTypes.set(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypes);
        entryTypes.set(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypes);
        
        MarketDataRequest.NoRelatedSym symbols = new MarketDataRequest.NoRelatedSym();
        symbols.set(new Symbol(currencyPair.toString()));
        request.addGroup(symbols);
        
        // TODO: Implement actual FIX message handling and ticker creation
        return future.join();
    }

    @Override
    public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
        CompletableFuture<OrderBook> future = new CompletableFuture<>();
        
        MarketDataRequest request = new MarketDataRequest();
        request.set(new MDReqID("ORDERBOOK_" + currencyPair.toString()));
        request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        request.set(new MarketDepth(10)); // Default depth of 10
        request.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        
        MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
        entryTypes.set(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypes);
        entryTypes.set(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypes);
        
        MarketDataRequest.NoRelatedSym symbols = new MarketDataRequest.NoRelatedSym();
        symbols.set(new Symbol(currencyPair.toString()));
        request.addGroup(symbols);
        
        // TODO: Implement actual FIX message handling and order book creation
        return future.join();
    }

    @Override
    public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {
        CompletableFuture<Trades> future = new CompletableFuture<>();
        
        MarketDataRequest request = new MarketDataRequest();
        request.set(new MDReqID("TRADES_" + currencyPair.toString()));
        request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        request.set(new MarketDepth(0));
        request.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        
        MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
        entryTypes.set(new MDEntryType(MDEntryType.TRADE));
        request.addGroup(entryTypes);
        
        MarketDataRequest.NoRelatedSym symbols = new MarketDataRequest.NoRelatedSym();
        symbols.set(new Symbol(currencyPair.toString()));
        request.addGroup(symbols);
        
        // TODO: Implement actual FIX message handling and trades creation
        return future.join();
    }
}