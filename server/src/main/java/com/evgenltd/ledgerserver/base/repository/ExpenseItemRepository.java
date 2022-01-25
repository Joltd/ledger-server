package com.evgenltd.ledgerserver.base.repository;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseItemRepository extends ReferenceRepository<ExpenseItem> {}