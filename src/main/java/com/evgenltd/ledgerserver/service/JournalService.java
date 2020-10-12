package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.record.CurrencyAmount;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalService(final JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public void persistDoubleJournalEntry(final JournalEntry debit, final JournalEntry credit) {
        // validate
        journalEntryRepository.save(debit);
        journalEntryRepository.save(credit);
    }

    public CurrencyAmount currentBalance(final LocalDate date, final Account account, final Currency currency) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndCurrency(
                date.atStartOfDay(),
                JournalEntry.Codes.C52,
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

}
