package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.Currency;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends ReferenceRepository<Currency> {}