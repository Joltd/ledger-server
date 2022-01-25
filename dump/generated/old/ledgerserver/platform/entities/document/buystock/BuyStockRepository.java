package old.ledgerserver.platform.entities.document.buystock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyStockRepository extends JpaRepository<BuyStock, Long>, JpaSpecificationExecutor<BuyStock> {}