package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByDateGreaterThanEqualAndDateLessThanAndCodeAndType(
            LocalDateTime from,
            LocalDateTime to,
            String code,
            JournalEntry.Type type
    );

    List<JournalEntry> findByDateLessThanAndCodeAndAccountAndCurrency(
            LocalDateTime date,
            String code,
            Account account,
            Currency currency
    );

    List<JournalEntry> findByDateLessThanAndCodeAndAccountAndTickerSymbol(
            LocalDateTime date,
            String code,
            Account account,
            TickerSymbol tickerSymbol
    );

    void deleteByDocumentId(Long documentId);

    List<JournalEntry> findByDateLessThanEqual(LocalDateTime date);

    List<JournalEntry> findByCode(String code);

}
