package com.evgenltd.ledgerserver.base.repository;

import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeItemRepository extends ReferenceRepository<IncomeItem> {}