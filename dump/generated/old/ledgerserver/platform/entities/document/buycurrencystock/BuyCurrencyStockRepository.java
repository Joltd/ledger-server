package old.ledgerserver.platform.entities.document.buycurrencystock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyCurrencyStockRepository extends JpaRepository<BuyCurrencyStock, Long>, JpaSpecificationExecutor<BuyCurrencyStock> {}