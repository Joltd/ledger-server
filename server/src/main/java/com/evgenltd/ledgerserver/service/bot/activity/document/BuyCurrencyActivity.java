package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.platform.entities.reference.account.Account;
import com.evgenltd.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.platform.entities.reference.expenseitem.ExpenseItem;
import com.evgenltd.ledgerserver.util.Utils;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyActivity extends DocumentActivity {

    private static final String DATE = "date";
    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_RATE = "currencyRate";
    private static final String CURRENCY_AMOUNT = "currencyAmount";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";

    private final SettingService settingService;

    public BuyCurrencyActivity(
            final SettingService settingService,
            final BeanFactory beanFactory
    ) {
        super(beanFactory);
        this.settingService = settingService;

        document().moneyField(AMOUNT);
        document().accountField(ACCOUNT);
        document().currencyField(CURRENCY);
        document().moneyField(CURRENCY_RATE);
        document().moneyField(CURRENCY_AMOUNT);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
    }

    @Override
    protected void onDefaults() {
//        document().set(CURRENCY, Currency.USD);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(AMOUNT, BigDecimal.ZERO);
        document().set(CURRENCY_RATE, BigDecimal.ZERO);
        document().set(CURRENCY_AMOUNT, BigDecimal.ZERO);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final BigDecimal amount = document().get(AMOUNT);
        final Account account = document().get(ACCOUNT);
        final Currency currency = document().get(CURRENCY);
        final BigDecimal currencyRate = document().get(CURRENCY_RATE);
        final BigDecimal currencyAmount = document().get(CURRENCY_AMOUNT);
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);

        document().dt52(date, amount, account, currency, currencyRate, currencyAmount);
        document().ct51(date, amount, account);

        document().dt91(date, commissionAmount, account, null, currency, commission);
        document().ct51(date, commissionAmount, account);

//        document().setComment("Buy %s %s", Utils.formatMoney(currencyAmount), currency.name());
    }

}
