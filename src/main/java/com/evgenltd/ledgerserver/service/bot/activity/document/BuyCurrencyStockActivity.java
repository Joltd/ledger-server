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
public class BuyCurrencyStockActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_RATE = "currencyRate";
    private static final String CURRENCY_AMOUNT = "currencyAmount";

    private final SettingService settingService;


    public BuyCurrencyStockActivity(
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

        on(CURRENCY_RATE, this::recalculateAmount);
        on(CURRENCY_AMOUNT, this::recalculateAmount);
    }

    @Override
    protected void onDefaults() {
        set(CURRENCY, Currency.USD);
        set(AMOUNT, BigDecimal.ZERO);
        set(CURRENCY_RATE, BigDecimal.ZERO);
        set(CURRENCY_AMOUNT, BigDecimal.ZERO);
    }

    @Override
    protected void onSave() {
        reassessment52(ACCOUNT, CURRENCY, CURRENCY_RATE);
        reassessment58(ACCOUNT, TICKER, CURRENCY, CURRENCY_RATE, PRICE);

        dt58(AMOUNT, ACCOUNT, TICKER, PRICE, COUNT, CURRENCY, CURRENCY_RATE, CURRENCY_AMOUNT);
        ct52(AMOUNT, ACCOUNT, CURRENCY, CURRENCY_RATE, CURRENCY_AMOUNT);
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get(CURRENCY_RATE, BigDecimal.ZERO);
        final BigDecimal currencyAmount = get(CURRENCY_AMOUNT, BigDecimal.ZERO);
        set(AMOUNT, currencyAmount.multiply(currencyRate));
    }

}
