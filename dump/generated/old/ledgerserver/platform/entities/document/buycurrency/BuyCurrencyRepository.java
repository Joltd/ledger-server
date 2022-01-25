package old.ledgerserver.platform.entities.document.buycurrency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyCurrencyRepository extends JpaRepository<BuyCurrency, Long>, JpaSpecificationExecutor<BuyCurrency> {}