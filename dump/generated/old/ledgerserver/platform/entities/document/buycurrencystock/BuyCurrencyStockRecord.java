package old.ledgerserver.platform.entities.document.buycurrencystock;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record BuyCurrencyStockRecord(
    Long id,
    String name,
    ReferenceRecord account,
    ReferenceRecord ticker,
    BigDecimal price,
    Long count,
    ReferenceRecord currency,
    BigDecimal currencyAmount
) {}