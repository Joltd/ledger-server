package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseItemRepository extends ReferenceRepository<ExpenseItem> {}