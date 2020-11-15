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
public class SellCurrencyActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_RATE = "currencyRate";
    private static final String CURRENCY_AMOUNT = "currencyAmount";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";
    private static final String CURRENCY_SALE_INCOME = "currencySaleIncome";
    private static final String CURRENCY_SALE_EXPENSE = "currencySaleExpense";

    private final SettingService settingService;

    public SellCurrencyActivity(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super(beanFactory);
        this.settingService = settingService;

        moneyField(AMOUNT);
        accountField(ACCOUNT);
        currencyField(CURRENCY);
        moneyField(CURRENCY_RATE);
        moneyField(CURRENCY_AMOUNT);
        expenseItemField(COMMISSION);
        moneyField(COMMISSION_AMOUNT);
        incomeItemField(CURRENCY_SALE_INCOME);
        expenseItemField(CURRENCY_SALE_EXPENSE);

        on(CURRENCY_RATE, this::recalculateAmount);
        on(CURRENCY_AMOUNT, this::recalculateAmount);
    }

    @Override
    protected void onDefaults() {
        set(CURRENCY, Currency.USD);
        set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        set(AMOUNT, BigDecimal.ZERO);
        set(CURRENCY_RATE, BigDecimal.ZERO);
        set(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(CURRENCY_SALE_INCOME, settingService.get(Settings.CURRENCY_SALE_INCOME_ITEM));
        set(CURRENCY_SALE_EXPENSE, settingService.get(Settings.CURRENCY_SALE_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        reassessment52(ACCOUNT, CURRENCY, CURRENCY_RATE);

        dt91(AMOUNT, CURRENCY_SALE_EXPENSE);
        ct52(AMOUNT, ACCOUNT, CURRENCY, CURRENCY_RATE, CURRENCY_AMOUNT);

        dt51(AMOUNT, ACCOUNT);
        ct91(AMOUNT, CURRENCY_SALE_INCOME);

        dt91(COMMISSION_AMOUNT, COMMISSION);
        ct51(COMMISSION_AMOUNT, ACCOUNT);
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get(CURRENCY_RATE, BigDecimal.ZERO);
        final BigDecimal currencyAmount = get(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(AMOUNT, currencyAmount.multiply(currencyRate));
    }

}
