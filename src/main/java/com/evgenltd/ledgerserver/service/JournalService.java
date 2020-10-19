package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.builder.JournalEntryBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.record.CurrencyAmount;
import com.evgenltd.ledgerserver.repository.DocumentRepository;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class JournalService {

    private final SettingService settingService;
    private final JournalEntryRepository journalEntryRepository;
    private final DocumentRepository documentRepository;

    public JournalService(
            final SettingService settingService,
            final JournalEntryRepository journalEntryRepository,
            final DocumentRepository documentRepository
    ) {
        this.settingService = settingService;
        this.journalEntryRepository = journalEntryRepository;
        this.documentRepository = documentRepository;
    }

    public void persistDocument(final Document document, final List<JournalEntry> journalEntries) {
        documentRepository.save(document);
        journalEntryRepository.deleteByDocumentId(document.getId());
        for (final JournalEntry journalEntry : journalEntries) {
            journalEntryRepository.save(journalEntry);
        }
    }

    public void persistDoubleJournalEntry(final JournalEntry debit, final JournalEntry credit) {
        // validate
        journalEntryRepository.save(debit);
        journalEntryRepository.save(credit);
    }

    public CurrencyAmount currentBalance(final LocalDate date, final Account account, final Currency currency) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndCurrency(
                date.atStartOfDay(),
                Codes.C52,
                account,
                currency
        );

        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal currencyBalance = BigDecimal.ZERO;
        for (final JournalEntry entry : result) {
            switch (entry.getType()) {
                case DEBIT -> {
                    balance = balance.add(entry.getAmount());
                    currencyBalance = currencyBalance.add(entry.getCurrencyAmount());
                }
                case CREDIT -> {
                    balance = balance.subtract(entry.getAmount());
                    currencyBalance = currencyBalance.subtract(entry.getCurrencyAmount());
                }
            }
        }

        return new CurrencyAmount(balance, currencyBalance);
    }

    // ##################################################
    // #                                                #
    // #  Common operations                             #
    // #                                                #
    // ##################################################

    public List<JournalEntry> transferToPerson(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Person person,
            final ExpenseItem expenseItem
    ) {
        final List<JournalEntry> result = new ArrayList<>();

        {
            final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C76, amount);
            debit.setPerson(person);
            result.add(debit);

            final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C51, amount);
            credit.setAccount(account);
            result.add(credit);
        }

        {
            final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C91_2, amount);
            debit.setExpenseItem(expenseItem);
            result.add(debit);

            final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C76, amount);
            credit.setPerson(person);
            result.add(credit);
        }

        return result;
    }

    public List<JournalEntry> transferFromPerson(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Person person,
            final IncomeItem incomeItem
    ) {
        final List<JournalEntry> result = new ArrayList<>();

        {
            final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C51, amount);
            debit.setAccount(account);
            result.add(debit);

            final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C76, amount);
            credit.setPerson(person);
            result.add(credit);
        }

        {
            final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C76, amount);
            debit.setPerson(person);
            result.add(debit);

            final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C91_1, amount);
            credit.setIncomeItem(incomeItem);
            result.add(credit);
        }

        return result;
    }

    public List<JournalEntry> convertToCurrency(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C52, amount);
        debit.setAccount(account);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);
        debit.setCurrencyAmount(currencyAmount);

        final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C51, amount);
        credit.setAccount(account);

        return Arrays.asList(debit, credit);
    }

    public List<JournalEntry> convertToStock(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C58, amount);
        debit.setAccount(account);
        debit.setTickerSymbol(ticker);
        debit.setPrice(price);
        debit.setCount(count);

        final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C51, amount);
        credit.setAccount(account);

        return Arrays.asList(debit, credit);
    }

    public List<JournalEntry> convertCurrencyToStock(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyAmount,
            final BigDecimal currencyRate,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C58, amount);
        debit.setAccount(account);
        debit.setTickerSymbol(ticker);
        debit.setPrice(price);
        debit.setCount(count);
        debit.setCurrencyAmount(currencyAmount);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);

        final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C52, amount);
        credit.setAccount(account);
        credit.setCurrencyAmount(currencyAmount);
        credit.setCurrency(currency);
        credit.setCurrencyRate(currencyRate);

        return Arrays.asList(debit, credit);
    }

    public List<JournalEntry> currencyReassessment(final LocalDateTime date, final Account account, final Currency currency, final BigDecimal newCurrencyRate) {
        final CurrencyAmount balance = currentBalance(date.toLocalDate().plusDays(1), account, currency);

        final BigDecimal actualBalance = balance.currencyAmount().multiply(newCurrencyRate);

        final BigDecimal diff = actualBalance.subtract(balance.amount());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            final IncomeItem reassessment = settingService.get(Settings.CURRENCY_REASSESSMENT_EXPENSE_ITEM);
            return currencyReassessmentIncrease(date, diff.abs(), account, currency, newCurrencyRate, reassessment);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            final ExpenseItem reassessment = settingService.get(Settings.CURRENCY_REASSESSMENT_INCOME_ITEM);
            return currencyReassessmentDecrease(date, diff.abs(), account, currency, newCurrencyRate, reassessment);
        } else {
            return Collections.emptyList();
        }
    }

    private List<JournalEntry> currencyReassessmentIncrease(final LocalDateTime date, final BigDecimal amount, final Account account, final Currency currency, final BigDecimal currencyRate, final IncomeItem reassessment) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, Codes.C52, amount);
        debit.setAccount(account);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);

        final JournalEntry credit = JournalEntryBuilder.credit(date, Codes.C91_1, amount);
        credit.setAccount(account);
        credit.setIncomeItem(reassessment);

        return Arrays.asList(debit, credit);
    }

    private List<JournalEntry> currencyReassessmentDecrease(final LocalDateTime date, final BigDecimal amount, final Account account, final Currency currency, final BigDecimal currencyRate, final ExpenseItem reassessment) {
        final JournalEntry debit = JournalEntryBuilder.credit(date, Codes.C91_2, amount);
        debit.setAccount(account);
        debit.setExpenseItem(reassessment);

        final JournalEntry credit = JournalEntryBuilder.debit(date, Codes.C52, amount);
        credit.setAccount(account);
        credit.setCurrency(currency);
        credit.setCurrencyRate(currencyRate);

        return Arrays.asList(debit, credit);
    }

}
