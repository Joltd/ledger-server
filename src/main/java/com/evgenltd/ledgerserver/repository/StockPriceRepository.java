package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    Optional<StockPrice> findByDateAndTicker(LocalDate date, String ticker);

}
