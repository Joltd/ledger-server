package com.evgenltd.ledgerserver.reference.repository;

import com.evgenltd.ledgerserver.reference.entity.ExpenseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, Long>, JpaSpecificationExecutor<ExpenseItem> {}
