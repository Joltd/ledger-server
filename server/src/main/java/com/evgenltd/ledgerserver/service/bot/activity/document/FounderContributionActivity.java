package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FounderContributionActivity extends DocumentActivity {

    private final static String AMOUNT = "amount";
    private final static String ACCOUNT = "account";

    public FounderContributionActivity(final BeanFactory beanFactory) {
        super(beanFactory);

        document().moneyField(AMOUNT);
        document().accountField(ACCOUNT);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final BigDecimal amount = document().get(AMOUNT);
        final Account account = document().get(ACCOUNT);

        document().dt75(date, amount);
        document().ct80(date, amount);

        document().dt51(date, amount, account);
        document().ct75(date, amount);

        document().setComment("Contribution %s", Utils.formatMoney(amount));
    }

}
