package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.IncomeItem;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.service.JournalService;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SellCurrencyActivity extends DocumentActivity {

    public static final String ACCOUNT = "account";
    public static final String CURRENCY = "currency";
    public static final String CURRENCY_RATE = "currencyRate";
    public static final String CURRENCY_AMOUNT = "currencyAmount";
    public static final String COMMISSION = "commission";
    public static final String COMMISSION_AMOUNT = "commissionAmount";
    public static final String CURRENCY_SALE_INCOME = "currencySaleIncome";
    public static final String CURRENCY_SALE_EXPENSE = "currencySaleExpense";

    private final SettingService settingService;
    private final JournalService journalService;

    public SellCurrencyActivity(
            final BeanFactory beanFactory,
            final SettingService settingService,
            final JournalService journalService
    ) {
        super(beanFactory);
        this.settingService = settingService;
        this.journalService = journalService;

        document().accountField(ACCOUNT);
        document().currencyField(CURRENCY);
        document().moneyField(CURRENCY_RATE);
        document().moneyField(CURRENCY_AMOUNT);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
        document().incomeItemField(CURRENCY_SALE_INCOME);
        document().expenseItemField(CURRENCY_SALE_EXPENSE);
    }

    @Override
    protected void onDefaults() {
        document().set(CURRENCY, Currency.USD);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(CURRENCY_RATE, BigDecimal.ZERO);
        document().set(CURRENCY_AMOUNT, BigDecimal.ZERO);
        document().set(CURRENCY_SALE_INCOME, settingService.get(Settings.CURRENCY_SALE_INCOME_ITEM));
        document().set(CURRENCY_SALE_EXPENSE, settingService.get(Settings.CURRENCY_SALE_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final Account account = document().get(ACCOUNT);
        final Currency currency = document().get(CURRENCY);
        final BigDecimal currencyRate = document().get(CURRENCY_RATE);
        final BigDecimal currencyAmount = document().get(CURRENCY_AMOUNT);
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);
        final IncomeItem currencySaleIncome = document().get(CURRENCY_SALE_INCOME);
        final ExpenseItem currencySaleExpense = document().get(CURRENCY_SALE_EXPENSE);

        final StockBalance balance = journalService.balance(date, account, currency);
        final BigDecimal averageCurrencyRate = balance.balance().divide(balance.currencyBalance(), RoundingMode.HALF_DOWN);
        final BigDecimal withdrawAmount = averageCurrencyRate.multiply(currencyAmount);

        document().dt51(date, withdrawAmount, account);
        document().ct52(date, withdrawAmount, account, currency, averageCurrencyRate, currencyAmount);

        final BigDecimal actualAmount = currencyRate.multiply(currencyAmount);
        final BigDecimal diff = actualAmount.subtract(withdrawAmount);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            document().dt51(date, diff, account);
            document().ct91(date, diff, account, null, currency, currencySaleIncome);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            document().dt91(date, diff.abs(), account, null, currency, currencySaleExpense);
            document().ct51(date, diff.abs(), account);
        }

        document().dt91(date, commissionAmount, account, null, currency, commission);
        document().ct51(date, commissionAmount, account);

        document().setComment("Sell %s %s", Utils.formatMoney(currencyAmount), currency.name());
    }

}
