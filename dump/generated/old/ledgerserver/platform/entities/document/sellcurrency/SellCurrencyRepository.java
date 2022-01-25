package old.ledgerserver.platform.entities.document.sellcurrency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SellCurrencyRepository extends JpaRepository<SellCurrency, Long>, JpaSpecificationExecutor<SellCurrency> {}