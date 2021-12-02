package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.service.JournalService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyStockActivity extends DocumentActivity {

    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_AMOUNT = "currencyAmount";

    private final JournalService journalService;

    public BuyCurrencyStockActivity(
            final BeanFactory beanFactory,
            final JournalService journalService
    ) {
        super(beanFactory);
        this.journalService = journalService;

        document().accountField(ACCOUNT);
        document().tickerField(TICKER);
        document().moneyField(PRICE);
        document().longField(COUNT);
        document().currencyField(CURRENCY);
        document().moneyField(CURRENCY_AMOUNT);
    }

    @Override
    protected void onDefaults() {
        document().set(CURRENCY, Currency.USD);
        document().set(CURRENCY_AMOUNT, BigDecimal.ZERO);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final Account account = document().get(ACCOUNT);
        final TickerSymbol ticker = document().get(TICKER);
        final BigDecimal price = document().get(PRICE);
        final Long count = document().get(COUNT);
        final Currency currency = document().get(CURRENCY);
        final BigDecimal currencyAmount = document().get(CURRENCY_AMOUNT);

        final StockBalance balance = journalService.balance(date, account, currency);
        final BigDecimal averageCurrencyRate = balance.balance().divide(balance.currencyBalance(), RoundingMode.HALF_DOWN);
        final BigDecimal withdrawAmount = averageCurrencyRate.multiply(currencyAmount);

        document().dt58(date, withdrawAmount, account, ticker, price, count, currency, averageCurrencyRate, currencyAmount);
        document().ct52(date, withdrawAmount, account, currency, averageCurrencyRate, currencyAmount);

        // commission

        document().setComment("Buy %s %s", count, ticker.getName());
    }

}
