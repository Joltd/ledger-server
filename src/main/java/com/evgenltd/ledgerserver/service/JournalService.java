package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.Document;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.record.CurrencyAmount;
import com.evgenltd.ledgerserver.repository.DocumentRepository;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final DocumentRepository documentRepository;

    public JournalService(
            final JournalEntryRepository journalEntryRepository,
            final DocumentRepository documentRepository
    ) {
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

}
