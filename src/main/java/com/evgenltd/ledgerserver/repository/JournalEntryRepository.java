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

    List<JournalEntry> findByDocumentId(Long id);

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

    void deleteByDocumentId(Long documentId);

}
