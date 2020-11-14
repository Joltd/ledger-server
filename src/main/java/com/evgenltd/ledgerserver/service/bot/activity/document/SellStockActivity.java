package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.service.bot.BotService;
import com.evgenltd.ledgerserver.service.brocker.CommissionCalculator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.evgenltd.ledgerserver.state.DocumentState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SellStockActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";
    private static final String STOCK_SALE_INCOME = "stockSaleIncome";
    private static final String STOCK_SALE_EXPENSE = "stockSaleExpense";

    private final SettingService settingService;

    public SellStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super( beanFactory);
        this.settingService = settingService;

        moneyField(AMOUNT);
        accountField(ACCOUNT);
        tickerField(TICKER);
        moneyField(PRICE);
        longField(COUNT);
        expenseItemField(COMMISSION);
        moneyField(COMMISSION_AMOUNT);
        incomeItemField(STOCK_SALE_INCOME);
        expenseItemField(STOCK_SALE_EXPENSE);

        on(PRICE, this::recalculateAmount);
        on(COUNT, this::recalculateAmount);
    }

    @Override
    protected void onDefaults() {
        set(AMOUNT, BigDecimal.ZERO);
        set(PRICE, BigDecimal.ZERO);
        set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        set(COUNT, 0L);
        set(STOCK_SALE_INCOME, settingService.get(Settings.STOCK_SALE_INCOME_ITEM));
        set(STOCK_SALE_EXPENSE, settingService.get(Settings.STOCK_SALE_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        reassessment58(ACCOUNT, TICKER, PRICE);

        dt91(AMOUNT, STOCK_SALE_EXPENSE);
        ct58(AMOUNT, ACCOUNT, TICKER, PRICE, COUNT, null, null, null);

        dt51(AMOUNT, ACCOUNT);
        ct91(AMOUNT, STOCK_SALE_INCOME);

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
        final BigDecimal price = get(PRICE, BigDecimal.ZERO);
        final Long count = get(COUNT, 0L);
        set(AMOUNT, price.multiply(new BigDecimal(count)));
    }

    private void recalculateCommissionAmount() {
        final CommissionCalculator calculator = settingService.get(Settings.BROKER_COMMISSION_CALCULATOR);
        final BigDecimal amount = get(AMOUNT, BigDecimal.ZERO);
        final BigDecimal commission = calculator.calculate(get("date"), amount);
        set(COMMISSION_AMOUNT, commission);
    }
}