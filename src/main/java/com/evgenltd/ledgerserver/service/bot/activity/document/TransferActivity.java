package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.builder.JournalEntryBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferActivity extends DocumentActivity {

    public TransferActivity(
            final BotService botService,
            final BeanFactory beanFactory
    ) {
        super(botService, beanFactory);

        moneyField("amount");
        accountField("from");
        accountField("to");
    }

    @Override
    protected void onSetup() {
        final JournalEntry credit = byCreditCode(Codes.C51);
        final JournalEntry debit = byDebitCode(Codes.C51);
        set("amount", debit.getAmount());
        set("from", credit.getAccount());
        set("to", debit.getAccount());
    }

    @Override
    protected void onSave() {
        final JournalEntry debit = JournalEntryBuilder.debit(get("date"), Codes.C51, get("amount"));
        debit.setAccount(get("to"));
        getJournalEntries().add(debit);

        final JournalEntry credit = JournalEntryBuilder.credit(get("date"), Codes.C51, get("amount"));
        credit.setAccount(get("from"));
        getJournalEntries().add(credit);
    }
}
