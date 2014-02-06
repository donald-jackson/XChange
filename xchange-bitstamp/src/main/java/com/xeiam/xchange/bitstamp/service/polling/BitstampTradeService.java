/**
 * Copyright (C) 2012 - 2014 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.bitstamp.service.polling;

import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.bitstamp.BitstampAdapters;
import com.xeiam.xchange.bitstamp.dto.trade.BitstampOrder;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.service.polling.PollingTradeService;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType.BID;

/**
 * @author Matija Mazi
 */
public class BitstampTradeService extends BitstampTradeServiceRaw implements PollingTradeService {

    /**
     * Constructor
     *
     * @param exchangeSpecification The {@link ExchangeSpecification}
     */
    public BitstampTradeService(ExchangeSpecification exchangeSpecification) {
        super(exchangeSpecification);
    }

    @Override
    public OpenOrders getOpenOrders() throws IOException {

        BitstampOrder[] openOrders = getBitstampOpenOrders();

        List<LimitOrder> limitOrders = new ArrayList<LimitOrder>();
        for (BitstampOrder bitstampOrder : openOrders) {
            OrderType orderType = bitstampOrder.getType() == 0 ? OrderType.BID : OrderType.ASK;
            String id = Integer.toString(bitstampOrder.getId());
            BigMoney price = BigMoney.of(CurrencyUnit.USD, bitstampOrder.getPrice());
            limitOrders.add(new LimitOrder(orderType, bitstampOrder.getAmount(), "BTC", "USD", id, bitstampOrder.getTime(), price));
        }
        return new OpenOrders(limitOrders);
    }

    @Override
    public String placeMarketOrder(MarketOrder marketOrder) throws IOException {

        throw new NotAvailableFromExchangeException();
    }

    @Override
    public String placeLimitOrder(LimitOrder limitOrder) throws IOException {

        BitstampOrder bitstampOrder;
        if (limitOrder.getType() == BID) {
            bitstampOrder = buyBitStampOrder(limitOrder.getTradableAmount(), limitOrder.getLimitPrice().getAmount());
        } else {
            bitstampOrder = sellBitstampOrder(limitOrder.getTradableAmount(), limitOrder.getLimitPrice().getAmount());
        }
        if (bitstampOrder.getErrorMessage() != null) {
            throw new ExchangeException(bitstampOrder.getErrorMessage());
        }

        return Integer.toString(bitstampOrder.getId());
    }

    @Override
    public boolean cancelOrder(String orderId) throws IOException {
        return cancelBitstampOrder(Integer.parseInt(orderId));
    }

    @Override
    public Trades getTradeHistory(final Object... arguments) throws IOException {

        Long numberOfTransactions = Long.MAX_VALUE;
        if (arguments.length > 0) {
            numberOfTransactions = (Long) arguments[0];
        }

        return BitstampAdapters.adaptTradeHistory(getBitstampUserTransactions(numberOfTransactions));
    }

}
