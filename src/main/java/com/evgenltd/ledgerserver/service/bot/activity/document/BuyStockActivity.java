package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
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
public class BuyStockActivity extends DocumentActivity {

    public static final String ACCOUNT = "account";
    public static final String TICKER = "ticker";
    public static final String PRICE = "price";
    public static final String COUNT = "count";
    public static final String COMMISSION = "commission";
    public static final String COMMISSION_AMOUNT = "commissionAmount";

    private final SettingService settingService;

    public BuyStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super(beanFactory);
        this.settingService = settingService;

        document().accountField(ACCOUNT);
        document().tickerField(TICKER);
        document().moneyField(PRICE);
        document().longField(COUNT);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
    }

    @Override
    protected void onDefaults() {
        document().set(PRICE, BigDecimal.ZERO);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(COUNT, 0L);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final Account account = document().get(ACCOUNT);
        final TickerSymbol ticker = document().get(TICKER);
        final BigDecimal price = document().get(PRICE);
        final Long count = document().get(COUNT);
        final BigDecimal amount = price.multiply(new BigDecimal(count));
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);


        document().dt58(date, amount, account, ticker, price, count, null, null, null);
        document().ct51(date, amount, account);

        document().dt91(date, commissionAmount, account, ticker, null, commission);
        document().ct51(date, commissionAmount, account);

        document().setComment("Buy %s %s", count, ticker.getName());
    }

}
