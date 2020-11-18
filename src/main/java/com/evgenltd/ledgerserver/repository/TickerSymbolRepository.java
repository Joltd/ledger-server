package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.Person;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerSymbolRepository extends JpaRepository<TickerSymbol, Long> {

    TickerSymbol findByName(String name);

}
