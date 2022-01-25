package old.ledgerserver.platform.entities.document.sellcurrencystock;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;
import java.lang.Boolean;

public record SellCurrencyStockRecord(
    Long id,
    String name,
    ReferenceRecord account,
    ReferenceRecord ticker,
    BigDecimal price,
    Long count,
    BigDecimal currencyRate,
    ReferenceRecord currency,
    ReferenceRecord commission,
    BigDecimal commissionAmount,
    ReferenceRecord stockSaleIncome,
    ReferenceRecord stockSaleExpense,
    Boolean directSelling
) {}