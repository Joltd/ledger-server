package old.ledgerserver.platform.entities.document.sellstock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SellStockRepository extends JpaRepository<SellStock, Long>, JpaSpecificationExecutor<SellStock> {}