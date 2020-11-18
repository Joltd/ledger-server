package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalService(final JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public StockBalance balance(final LocalDateTime date, final Account account, final TickerSymbol tickerSymbol) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndTickerSymbol(
                date,
                Codes.C58,
                account,
                tickerSymbol
        );

        return calculateStockBalance(result);
    }

    public StockBalance balance(final LocalDateTime date, final Account account, final Currency currency) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndCurrency(
                date,
                Codes.C52,
                account,
                currency
        );

        return calculateStockBalance(result);
    }

    @NotNull
    private StockBalance calculateStockBalance(final List<JournalEntry> result) {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal currencyBalance = BigDecimal.ZERO;
        long count = 0L;
        for (JournalEntry entry : result) {
            switch (entry.getType()) {
                case DEBIT -> {
                    balance = balance.add(entry.getAmount());
                    final BigDecimal currencyAmount = entry.getCurrencyAmount() != null
                            ? entry.getCurrencyAmount()
                            : BigDecimal.ZERO;
                    currencyBalance = currencyBalance.add(currencyAmount);
                    count += entry.getCount() != null ? entry.getCount() : 0L;
                }
                case CREDIT -> {
                    balance = balance.subtract(entry.getAmount());
                    final BigDecimal currencyAmount = entry.getCurrencyAmount() != null
                            ? entry.getCurrencyAmount()
                            : BigDecimal.ZERO;
                    currencyBalance = currencyBalance.subtract(currencyAmount);
                    count -= entry.getCount() != null ? entry.getCount() : 0L;
                }
            }
        }

        return new StockBalance(balance, currencyBalance, count);
    }

}
