package com.evgenltd.ledgerserver.document.impl;

import com.evgenltd.ledgerserver.document.common.entity.TypeConstant;
import com.evgenltd.ledgerserver.document.common.service.DocumentDefinition;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.reference.ReferenceModel;
import com.evgenltd.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol;
import com.evgenltd.ledgerserver.reference.repository.TickerSymbolRepository;
import com.evgenltd.ledgerserver.service.JournalService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Component(TypeConstant.BUY_CURRENCY_STOCK)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyStockDefinition extends DocumentDefinition {

    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_AMOUNT = "currencyAmount";

    private final JournalService journalService;
    private final TickerSymbolRepository tickerSymbolRepository;

    public BuyCurrencyStockDefinition(
            final JournalService journalService,
            final TickerSymbolRepository tickerSymbolRepository
    ) {
        this.journalService = journalService;
        this.tickerSymbolRepository = tickerSymbolRepository;
    }

    @Override
    public List<MetaField> meta() {
        return Arrays.asList(
                ReferenceModel.account(ACCOUNT),
                ReferenceModel.tickerSymbol(TICKER),
                ReferenceModel.money(PRICE),
                ReferenceModel.asLong(COUNT),
                ReferenceModel.currency(CURRENCY),
                ReferenceModel.money(CURRENCY_AMOUNT)
        );
    }

    @Override
    public void persist() {
        final Long account = asLong(ACCOUNT);
        final Long ticker = asLong(TICKER);
        final BigDecimal price = asMoney(PRICE);
        final Long count = asLong(COUNT);
        final Currency currency = asEnum(Currency.class, CURRENCY);
        final BigDecimal currencyAmount = asMoney(CURRENCY_AMOUNT);

        final StockBalance balance = new StockBalance(null, null, null);//journalService.balance(document().getDate(), account, currency);
        final BigDecimal averageCurrencyRate = balance.balance().divide(balance.currencyBalance(), RoundingMode.HALF_DOWN);
        final BigDecimal withdrawAmount = averageCurrencyRate.multiply(currencyAmount);

        dt58(withdrawAmount, account, ticker, price, count, currency, averageCurrencyRate, currencyAmount);
        ct52(withdrawAmount, account, currency, averageCurrencyRate, currencyAmount);

        // commission

        final TickerSymbol tickerValue = tickerSymbolRepository.getById(ticker);
        comment("Buy %s %s", count, tickerValue.getName());
    }
}
