package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.IncomeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeItemRepository extends JpaRepository<IncomeItem, Long> {
}
