package com.evgenltd.ledgerserver.reference.repository;

import com.evgenltd.ledgerserver.reference.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerSymbolRepository extends JpaRepository<TickerSymbol, Long>, JpaSpecificationExecutor<TickerSymbol> {}
