package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalService(final JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

//    public StockBalance balance(final LocalDateTime date, final Account account, final TickerSymbol tickerSymbol) {
//        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndTickerSymbol(
//                date,
//                Codes.C58,
//                account,
//                tickerSymbol
//        );
//
//        return calculateStockBalance(result);
//    }
//
//    public StockBalance balance(final LocalDateTime date, final Account account, final Currency currency) {
//        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndCurrency(
//                date,
//                Codes.C52,
//                account,
//                currency
//        );
//
//        return calculateStockBalance(result);
//    }
//
//    @NotNull
//    private StockBalance calculateStockBalance(final List<JournalEntry> result) {
//        return result.stream()
//                .map(entry -> new StockBalance(entry.amount(), entry.currencyAmount(), entry.count()))
//                .reduce(
//                        new StockBalance(BigDecimal.ZERO, BigDecimal.ZERO, 0L),
//                        (left, right) -> new StockBalance(
//                                left.balance().add(right.balance()),
//                                left.currencyBalance().add(right.currencyBalance()),
//                                left.count() + right.count()
//                        )
//                );
//    }

}
