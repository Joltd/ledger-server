package com.evgenltd.ledgerserver.service.bot.document;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.builder.JournalEntryBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.service.bot.BotService;
import com.evgenltd.ledgerserver.service.brocker.CommissionCalculator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyActivity extends DocumentActivity {

    private final SettingService settingService;

    public BuyCurrencyActivity(
            final BotService botService,
            final SettingService settingService,
            final BeanFactory beanFactory
    ) {
        super(botService, beanFactory);
        this.settingService = settingService;

        moneyField("amount");
        accountField("account");
        personField("broker");
        currencyField("currency");
        moneyField("currencyRate");
        moneyField("currencyAmount");
        expenseItemField("commission");
        moneyField("commissionAmount");

        on("currencyRate", this::recalculateAmount);
        on("currencyAmount", this::recalculateAmount);
    }

    @Override
    protected void onSetup() {
        final JournalEntry currencyEntry = byDebitCode(Codes.C52);
        set("amount", currencyEntry.getAmount());
        set("account", currencyEntry.getAccount());
        set("currency", currencyEntry.getCurrency());
        set("currencyRate", currencyEntry.getCurrencyRate());
        set("currencyAmount", currencyEntry.getCurrencyAmount());

        final JournalEntry personEntry = byDebitCode(Codes.C76);
        set("broker", personEntry.getPerson());

        final JournalEntry commissionEntry = byDebitCode(Codes.C91_2);
        set("commission", commissionEntry.getExpenseItem());
        set("commissionAmount", commissionEntry.getAmount());
    }

    @Override
    protected void onDefaults() {
        set("currency", Currency.USD);
        set("broker", settingService.get(Settings.BROKER));
        set("commission", settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        set("amount", BigDecimal.ZERO);
        set("currencyRate", BigDecimal.ZERO);
        set("currencyAmount", BigDecimal.ZERO);
    }

    @Override
    protected void onSave() {
        moveToCurrency();
        moveToPerson();
        personAsExpense();
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
        final BigDecimal currencyRate = get("currencyRate");
        final BigDecimal currencyAmount = get("currencyAmount");
        set("amount", currencyAmount.multiply(currencyRate));
    }

    private void recalculateCommissionAmount() {
        final CommissionCalculator calculator = settingService.get(Settings.BROKER_COMMISSION_CALCULATOR);
        final BigDecimal amount = get("amount");
        final BigDecimal commission = calculator.calculate(get("date"), amount);
        set("commissionAmount", commission);
    }

    private void moveToCurrency() {
        final JournalEntry debit = JournalEntryBuilder.debit(
                get("date"),
                Codes.C52,
                get("amount")
        );
        debit.setAccount(get("account"));
        debit.setCurrency(get("currency"));
        debit.setCurrencyRate(get("currencyRate"));
        debit.setCurrencyAmount(get("currencyAmount"));
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
