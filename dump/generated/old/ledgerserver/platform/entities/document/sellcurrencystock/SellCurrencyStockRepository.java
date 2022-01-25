package old.ledgerserver.platform.entities.document.sellcurrencystock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SellCurrencyStockRepository extends JpaRepository<SellCurrencyStock, Long>, JpaSpecificationExecutor<SellCurrencyStock> {}