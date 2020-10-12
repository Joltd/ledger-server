package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByDateGreaterThanEqualAndDateLessThanAndCodeAndType(
            final LocalDateTime from,
            final LocalDateTime to,
            final String code,
            final JournalEntry.Type type
    );

    List<JournalEntry> findByDateLessThanAndCodeAndAccountAndCurrency(
            final LocalDateTime date,
            final String code,
            final Account account,
            final Currency currency
    );

}
