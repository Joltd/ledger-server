package com.evgenltd.ledgerserver.stonks.repository;

import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerSymbolRepository extends ReferenceRepository<TickerSymbol> {}