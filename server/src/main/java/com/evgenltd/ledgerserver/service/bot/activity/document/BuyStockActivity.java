package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.platform.entities.reference.account.Account;
import com.evgenltd.ledgerserver.platform.entities.reference.expenseitem.ExpenseItem;
import com.evgenltd.ledgerserver.platform.entities.reference.tickersymbol.TickerSymbol;
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

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String TICKER = "ticker";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";

    private final SettingService settingService;

    public BuyStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super(beanFactory);
        this.settingService = settingService;

        document().moneyField(AMOUNT);
        document().accountField(ACCOUNT);
        document().tickerField(TICKER);
        document().moneyField(PRICE);
        document().longField(COUNT);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
    }

    @Override
    protected void onDefaults() {
        document().set(AMOUNT, BigDecimal.ZERO);
        document().set(PRICE, BigDecimal.ZERO);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(COUNT, 0L);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final BigDecimal amount = document().get(AMOUNT);
        final Account account = document().get(ACCOUNT);
        final TickerSymbol ticker = document().get(TICKER);
        final BigDecimal price = document().get(PRICE);
        final Long count = document().get(COUNT);
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);


        document().dt58(date, amount, account, ticker, price, count, null, null, null);
        document().ct51(date, amount, account);

        document().dt91(date, commissionAmount, account, ticker, null, commission);
        document().ct51(date, commissionAmount, account);

        document().setComment("Buy %s %s", count, ticker.getName());
    }

}
