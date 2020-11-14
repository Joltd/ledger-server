package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.evgenltd.ledgerserver.state.DocumentState.*;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String FROM = "from";
    private static final String TO = "to";

    public TransferActivity(final BeanFactory beanFactory) {
        super(beanFactory);

        moneyField(AMOUNT);
        accountField(FROM);
        accountField(TO);
    }

    @Override
    protected void onSave() {
        dt51(AMOUNT, TO);
        ct51(AMOUNT, FROM);
    }
}
