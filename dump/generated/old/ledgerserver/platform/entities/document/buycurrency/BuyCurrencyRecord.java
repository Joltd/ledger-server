package old.ledgerserver.platform.entities.document.buycurrency;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.lang.String;
import java.math.BigDecimal;

public record BuyCurrencyRecord(
    Long id,
    String name,
    BigDecimal amount,
    ReferenceRecord account,
    ReferenceRecord currency,
    BigDecimal currencyRate,
    BigDecimal currencyAmount,
    ReferenceRecord commission,
    BigDecimal commissionAmount
) {}