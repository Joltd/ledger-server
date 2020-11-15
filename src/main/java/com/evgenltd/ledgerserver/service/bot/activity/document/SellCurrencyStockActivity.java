package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.evgenltd.ledgerserver.service.bot.DocumentState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SellCurrencyStockActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_RATE = "currencyRate";
    private static final String CURRENCY_AMOUNT = "currencyAmount";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";
    private static final String STOCK_SALE_INCOME = "stockSaleIncome";
    private static final String STOCK_SALE_EXPENSE = "stockSaleExpense";

    private final SettingService settingService;

    public SellCurrencyStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super(beanFactory);
        this.settingService = settingService;

        moneyField(AMOUNT);
        accountField(ACCOUNT);
        tickerField(TICKER);
        moneyField(PRICE);
        longField(COUNT);

        currencyField(CURRENCY);
        moneyField(CURRENCY_RATE);
        moneyField(CURRENCY_AMOUNT);

        incomeItemField(STOCK_SALE_INCOME);
        expenseItemField(STOCK_SALE_EXPENSE);

        on(CURRENCY_RATE, this::recalculateAmount);
        on(CURRENCY_AMOUNT, this::recalculateAmount);
    }

    @Override
    protected void onDefaults() {
        set(CURRENCY, Currency.USD);
        set(AMOUNT, BigDecimal.ZERO);
        set(CURRENCY_RATE, BigDecimal.ZERO);
        set(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(COUNT, 0L);
        set(STOCK_SALE_INCOME, settingService.get(Settings.STOCK_SALE_INCOME_ITEM));
        set(STOCK_SALE_EXPENSE, settingService.get(Settings.STOCK_SALE_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        reassessment52(ACCOUNT, CURRENCY, CURRENCY_RATE);
        reassessment58(ACCOUNT, TICKER, CURRENCY, CURRENCY_RATE, PRICE);

        dt91(AMOUNT, STOCK_SALE_EXPENSE);
        ct58(AMOUNT, ACCOUNT, TICKER, PRICE, COUNT, CURRENCY, CURRENCY_RATE, CURRENCY_AMOUNT);

        dt51(AMOUNT, ACCOUNT);
        ct91(AMOUNT, STOCK_SALE_INCOME);

        dt91(COMMISSION_AMOUNT, COMMISSION);
        ct51(COMMISSION_AMOUNT, ACCOUNT);
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get(CURRENCY_RATE, BigDecimal.ZERO);
        final BigDecimal currencyAmount = get(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(AMOUNT, currencyAmount.multiply(currencyRate));
    }

}
