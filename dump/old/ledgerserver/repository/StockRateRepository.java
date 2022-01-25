package old.ledgerserver.repository;

import old.ledgerserver.entity.StockRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StockRateRepository extends JpaRepository<StockRate, Long> {

    Optional<StockRate> findByDateAndTicker(LocalDate date, String ticker);

}
