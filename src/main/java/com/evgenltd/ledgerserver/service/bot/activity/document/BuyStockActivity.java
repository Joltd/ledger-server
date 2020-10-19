package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.builder.JournalEntryBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.service.JournalService;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.service.bot.BotService;
import com.evgenltd.ledgerserver.service.brocker.CommissionCalculator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyStockActivity extends DocumentActivity {

    private final SettingService settingService;
    private final JournalService journalService;

    public BuyStockActivity(
            final BotService botService,
            final BeanFactory beanFactory,
            final SettingService settingService,
            final JournalService journalService
    ) {
        super(botService, beanFactory);
        this.settingService = settingService;
        this.journalService = journalService;

        moneyField("amount");
        accountField("account");
        personField("broker");
        tickerField("ticker");
        moneyField("price");
        longField("count");
        expenseItemField("commission");
        moneyField("commissionAmount");

        on("price", this::recalculateAmount);
        on("count", this::recalculateAmount);
    }

    @Override
    protected void onSetup() {
        final JournalEntry stockEntry = byDebitCode(Codes.C58);
        set("amount", stockEntry.getAmount());
        set("account", stockEntry.getAccount());
        set("ticker", stockEntry.getTickerSymbol());
        set("price", stockEntry.getPrice());
        set("count", stockEntry.getCount());

        final JournalEntry personEntry = byDebitCode(Codes.C76);
        set("broker", personEntry.getPerson());

        final JournalEntry commissionEntry = byDebitCode(Codes.C91_2);
        set("commission", commissionEntry.getExpenseItem());
        set("commissionAmount", commissionEntry.getAmount());
    }

    @Override
    protected void onDefaults() {
        set("amount", BigDecimal.ZERO);
        set("price", BigDecimal.ZERO);
        set("count", 0);
    }

    @Override
    protected void onSave() {
        final List<JournalEntry> convertToStock = journalService.convertToStock(
                get("date"),
                get("amount"),
                get("account"),
                get("ticker"),
                get("price"),
                get("count")
        );
        final List<JournalEntry> transferToPerson = journalService.transferToPerson(
                get("date"),
                get("commissionAmount"),
                get("account"),
                get("person"),
                get("commission")
        );
        getJournalEntries().addAll(convertToStock);
        getJournalEntries().addAll(transferToPerson);
    }

    @Override
    protected void onMessageReceived(final String message) {
        super.onMessageReceived(message);
        final String command = splitFirstWord(message).word();
        if (Utils.isSimilar(command, "comCalc")) {
            recalculateCommissionAmount();
        }
    }

    private void recalculateAmount() {
        final BigDecimal currencyRate = get("price");
        final BigDecimal currencyAmount = get("count");
        set("amount", currencyAmount.multiply(currencyRate));
    }

    private void recalculateCommissionAmount() {
        final CommissionCalculator calculator = settingService.get(Settings.BROKER_COMMISSION_CALCULATOR);
        final BigDecimal amount = get("amount");
        final BigDecimal commission = calculator.calculate(get("date"), amount);
        set("commissionAmount", commission);
    }

    private void moveToStock() {
        final JournalEntry debit = JournalEntryBuilder.debit(
                get("date"),
                Codes.C58,
                get("amount")
        );
        debit.setAccount(get("account"));
        debit.setTickerSymbol(get("ticker"));
        debit.setPrice(get("price"));
        debit.setCount(get("count"));
        getJournalEntries().add(debit);

        final JournalEntry credit = JournalEntryBuilder.credit(
                get("date"),
                Codes.C51,
                get("amount")
        );
        credit.setAccount(get("account"));
        getJournalEntries().add(credit);
    }

    private void moveToPerson() {
        final JournalEntry debit = JournalEntryBuilder.debit(
                get("date"),
                Codes.C76,
                get("commissionAmount")
        );
        debit.setPerson(get("broker"));
        getJournalEntries().add(debit);

        final JournalEntry credit = JournalEntryBuilder.credit(
                get("date"),
                Codes.C51,
                get("commissionAmount")
        );
        credit.setAccount(get("account"));
        getJournalEntries().add(credit);
    }

    private void personAsExpense() {
        final JournalEntry debit = JournalEntryBuilder.debit(
                get("date"),
                Codes.C91_2,
                get("commissionAmount")
        );
        debit.setExpenseItem(get("commission"));
        getJournalEntries().add(debit);

        final JournalEntry credit = JournalEntryBuilder.credit(
                get("date"),
                Codes.C76,
                get("commissionAmount")
        );
        credit.setPerson(get("broker"));
        getJournalEntries().add(credit);
    }

}
