package old.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferActivity extends DocumentActivity {

    private static final String AMOUNT = "amount";
    private static final String FROM = "from";
    private static final String TO = "to";

    public TransferActivity(final BeanFactory beanFactory) {
        super(beanFactory);

        document().moneyField(AMOUNT);
        document().accountField(FROM);
        document().accountField(TO);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final BigDecimal amount = document().get(AMOUNT);
        final Account from = document().get(FROM);
        final Account to = document().get(TO);

        document().dt51(date, amount, to);
        document().ct51(date, amount, from);

        document().setComment("Move %s from '%s' to '%s'", Utils.formatMoney(amount), from.getName(), to.getName());
    }
}
