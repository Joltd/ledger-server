package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.service.brocker.tinkoff.TinkoffService;
import com.evgenltd.ledgerserver.util.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final JournalEntryRepository journalEntryRepository;
    private final TinkoffService tinkoffService;

    public DataService(
            final JournalEntryRepository journalEntryRepository,
            final TinkoffService tinkoffService
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.tinkoffService = tinkoffService;
    }

    public List<PortfolioEntry> portfolio() {
        return journalEntryRepository.findByCode(Codes.C58)
                .stream()
                .map(this::toPortfolioEntry)
                .collect(Collectors.groupingBy(
                        entry -> entry.ticker().getName(),
                        Collectors.reducing(
                                PortfolioEntry.empty(),
                                PortfolioEntry::combine
                        )
                ))
                .values()
                .stream()
                .map(this::loadPrice)
                .sorted(Comparator.comparing(o -> o.ticker().getName()))
                .collect(Collectors.toList());
    }

    private PortfolioEntry toPortfolioEntry(final JournalEntry journalEntry) {
        if (journalEntry.getType().equals(JournalEntry.Type.DEBIT)) {
            return new PortfolioEntry(
                    journalEntry.getTickerSymbol(),
                    journalEntry.getAmount(),
                    journalEntry.getCount()
            );
        } else {
            return new PortfolioEntry(
                    journalEntry.getTickerSymbol(),
                    journalEntry.getAmount().negate(),
                    -journalEntry.getCount()
            );
        }
    }

    private PortfolioEntry loadPrice(final PortfolioEntry entry) {
        final BigDecimal price = tinkoffService.price(entry.ticker().getFigi());

        return new PortfolioEntry(
                entry.ticker(),
                price.multiply(new BigDecimal(entry.count())),
                entry.count()
        );
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record PortfolioEntry(TickerSymbol ticker, BigDecimal balance, Long count) {
        public static PortfolioEntry empty() {
            return new PortfolioEntry(null, BigDecimal.ZERO, 0L);
        }

        public static PortfolioEntry combine(final PortfolioEntry left, final PortfolioEntry right) {
            return new PortfolioEntry(
                    Utils.ifNull(left.ticker(), right.ticker),
                    left.balance().add(right.balance()),
                    left.count() + right.count()
            );
        }
    }

}
