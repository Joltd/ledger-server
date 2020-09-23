package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.IncomeItem;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}
