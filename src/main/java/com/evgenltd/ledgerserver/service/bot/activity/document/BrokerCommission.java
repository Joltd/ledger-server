package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BrokerCommission extends DocumentActivity {

    public static final String AMOUNT = "amount";
    public static final String ACCOUNT = "account";
    public static final String COMMISSION = "commission";

    private final SettingService settingService;

    public BrokerCommission(
            final BeanFactory beanFactory,
            final SettingService settingService
    ) {
        super(beanFactory);
        this.settingService = settingService;

        document().moneyField(AMOUNT);
        document().accountField(ACCOUNT);
        document().expenseItemField(COMMISSION);
    }

    @Override
    protected void onDefaults() {
        document().set(AMOUNT, BigDecimal.ZERO);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final BigDecimal amount = document().get(AMOUNT);
        final Account account = document().get(ACCOUNT);
        final ExpenseItem commission = document().get(COMMISSION);

        document().dt91(date, amount, account, null, null, commission);
        document().ct51(date, amount, account);

        document().setComment("Broker commission %s", amount);
    }

}
