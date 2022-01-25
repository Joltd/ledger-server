package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.IncomeItem;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeItemRepository extends ReferenceRepository<IncomeItem> {}