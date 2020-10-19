package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
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
public class BuyCurrencyActivity extends DocumentActivity {

    private final SettingService settingService;
    private final JournalService journalService;

    public BuyCurrencyActivity(
            final BotService botService,
            final SettingService settingService,
            final BeanFactory beanFactory,
            final JournalService journalService
    ) {
        super(botService, beanFactory);
        this.settingService = settingService;
        this.journalService = journalService;

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
        final List<JournalEntry> convertToCurrency = journalService.convertToCurrency(
                get("date"),
                get("amount"),
                get("account"),
                get("currency"),
                get("currencyRate"),
                get("currencyAmount")
        );
        final List<JournalEntry> transferToPerson = journalService.transferToPerson(
                get("date"),
                get("commissionAmount"),
                get("account"),
                get("broker"),
                get("commission")
        );
        getJournalEntries().addAll(convertToCurrency);
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

}
