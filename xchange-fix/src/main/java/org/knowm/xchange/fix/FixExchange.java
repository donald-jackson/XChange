package org.knowm.xchange.fix;

import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.fix.service.FixMarketDataService;
import org.knowm.xchange.fix.service.FixTradeService;
import org.knowm.xchange.fix.service.FixAccountService;

public class FixExchange extends BaseExchange implements Exchange {

    @Override
    protected void initServices() {
        this.marketDataService = new FixMarketDataService(this);
        this.tradeService = new FixTradeService(this);
        this.accountService = new FixAccountService(this);
    }

    @Override
    public ExchangeSpecification getDefaultExchangeSpecification() {
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(this.getClass());
        exchangeSpecification.setSslUri("fix://localhost:9876");
        exchangeSpecification.setHost("localhost");
        exchangeSpecification.setPort(9876);
        exchangeSpecification.setExchangeName("FIX");
        exchangeSpecification.setExchangeDescription("Generic FIX Protocol Exchange");
        
        return exchangeSpecification;
    }
}