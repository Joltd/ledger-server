package old.ledgerserver.platform.entities.document.sellstock;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record SellStockRecord(
    Long id,
    String name,
    ReferenceRecord account,
    ReferenceRecord ticker,
    BigDecimal price,
    Long count,
    ReferenceRecord commission,
    BigDecimal commissionAmount,
    ReferenceRecord stockSaleIncome,
    ReferenceRecord stockSaleExpense
) {}