package old.ledgerserver.platform.entities.document.sellcurrency;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.lang.String;
import java.math.BigDecimal;

public record SellCurrencyRecord(
    Long id,
    String name,
    ReferenceRecord account,
    ReferenceRecord currency,
    BigDecimal currencyRate,
    BigDecimal currencyAmount,
    ReferenceRecord commission,
    BigDecimal commissionAmount,
    ReferenceRecord currencySaleIncome,
    ReferenceRecord currencySaleExpense
) {}