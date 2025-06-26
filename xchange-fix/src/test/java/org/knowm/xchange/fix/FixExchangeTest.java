package org.knowm.xchange.fix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;

public class FixExchangeTest {

    @Test
    public void testCreateExchange() {
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(FixExchange.class);
        assertThat(exchange).isNotNull();
        assertThat(exchange.getExchangeSpecification().getExchangeName()).isEqualTo("FIX");
    }
}