package com.evgenltd.ledgerserver.service.bot.activity.document;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.evgenltd.ledgerserver.service.bot.DocumentState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FounderContributionActivity extends DocumentActivity {

    private final static String AMOUNT = "amount";
    private final static String ACCOUNT = "account";

    public FounderContributionActivity(final BeanFactory beanFactory) {
        super(beanFactory);

        moneyField(AMOUNT);
        accountField(ACCOUNT);
    }

    @Override
    protected void onSave() {
        dt75(AMOUNT);
        ct80(AMOUNT);

        dt51(AMOUNT, ACCOUNT);
        ct75(AMOUNT);
    }

}
