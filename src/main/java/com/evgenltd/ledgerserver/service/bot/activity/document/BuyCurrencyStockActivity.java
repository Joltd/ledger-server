package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.service.JournalService;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyStockActivity extends DocumentActivity {

    private final JournalService journalService;

    public BuyCurrencyStockActivity(
            final BotService botService,
            final BeanFactory beanFactory,
            final JournalService journalService
    ) {
        super(botService, beanFactory);
        this.journalService = journalService;

        moneyField("amount");
        accountField("account");
        tickerField("ticker");
        moneyField("price");
        longField("count");

        currencyField("currency");
        moneyField("currencyRate");
        moneyField("currencyAmount");

        on("currencyRate", this::recalculateAmount);
        on("currencyAmount", this::recalculateAmount);
    }

    @Override
    protected void onSetup() {
        final JournalEntry stockEntry = byDebitCode(Codes.C58);
        set("amount", stockEntry.getAmount());
        set("account", stockEntry.getAccount());
        set("ticker", stockEntry.getTickerSymbol());
        set("price", stockEntry.getPrice());
        set("count", stockEntry.getCount());
        set("currency", stockEntry.getCurrency());
        set("currencyRate", stockEntry.getCurrencyRate());
        set("currencyAmount", stockEntry.getCurrencyAmount());
    }

    @Override
    protected void onDefaults() {
        set("currency", Currency.USD);
        set("amount", BigDecimal.ZERO);
        set("currencyRate", BigDecimal.ZERO);
        set("currencyAmount", BigDecimal.ZERO);
    }

    @Override
    protected void onSave() {
        final List<JournalEntry> currencyReassessment = journalService.currencyReassessment(
                get("date"),
                get("account"),
                get("currency"),
                get("currencyRate")
        );
        final List<JournalEntry> convertCurrencyToStock = journalService.convertCurrencyToStock(
                get("date"),
                get("amount"),
                get("account"),
                get("currency"),
                get("currencyAmount"),
                get("currencyRate"),
                get("ticker"),
                get("price"),
                get("count")
        );
        getJournalEntries().addAll(currencyReassessment);
        getJournalEntries().addAll(convertCurrencyToStock);
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get("currencyRate");
        final BigDecimal currencyAmount = get("currencyAmount");
        set("amount", currencyAmount.multiply(currencyRate));
    }

}
