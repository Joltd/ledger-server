package com.evgenltd.ledgerserver.base.repository;

import com.evgenltd.ledgerserver.base.entity.Currency;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends ReferenceRepository<Currency> {}