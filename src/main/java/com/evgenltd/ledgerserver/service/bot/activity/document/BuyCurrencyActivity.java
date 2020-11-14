package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.service.brocker.CommissionCalculator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.evgenltd.ledgerserver.state.DocumentState.*;

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

        moneyField(AMOUNT);
        accountField(ACCOUNT);
        currencyField(CURRENCY);
        moneyField(CURRENCY_RATE);
        moneyField(CURRENCY_AMOUNT);
        expenseItemField(COMMISSION);
        moneyField(COMMISSION_AMOUNT);

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
    }

    @Override
    protected void onSave() {
        dt52(AMOUNT, ACCOUNT, CURRENCY, CURRENCY_RATE, CURRENCY_AMOUNT);
        ct51(AMOUNT, ACCOUNT);

        dt91(COMMISSION_AMOUNT, COMMISSION);
        ct51(COMMISSION_AMOUNT, ACCOUNT);
    }

    @Override
    protected void onMessageReceived(final String message) {
        super.onMessageReceived(message);
        final String command = Tokenizer.of(message).next();
        if (Utils.isSimilar(command, "comCalc")) {
            recalculateCommissionAmount();
        }
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get(CURRENCY_RATE, BigDecimal.ZERO);
        final BigDecimal currencyAmount = get(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(AMOUNT, currencyAmount.multiply(currencyRate));
    }

    private void recalculateCommissionAmount() {
        final CommissionCalculator calculator = settingService.get(Settings.BROKER_COMMISSION_CALCULATOR);
        final BigDecimal amount = get(AMOUNT, BigDecimal.ZERO);
        final BigDecimal commission = calculator.calculate(get(DATE), amount);
        set(COMMISSION_AMOUNT, commission);
    }

}
