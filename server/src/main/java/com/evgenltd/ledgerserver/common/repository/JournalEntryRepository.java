package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.Currency;
import com.evgenltd.ledgerserver.common.entity.JournalEntry;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByDateGreaterThanEqualAndDateLessThanEqualAndCodeAndTypeAndAccountAndExpenseItem(
            LocalDateTime from,
            LocalDateTime to,
            String code,
            JournalEntry.Type type,
            Account account,
            ExpenseItem expenseItem
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

    List<JournalEntry> findByCodeLike(String code);

    List<JournalEntry> findByExpenseItemName(String commission);

    List<JournalEntry> findByDateLessThanEqualAndCodeLike(LocalDateTime date, String code);

}
