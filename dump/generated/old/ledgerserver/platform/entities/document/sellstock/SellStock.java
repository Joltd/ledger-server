package old.ledgerserver.platform.entities.document.sellstock;

import lombok.*;
import javax.persistence.*;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import java.math.BigDecimal;
import java.lang.Long;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;

@Entity
@Table(name = "document_sell_stock")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "ticker_id")
    private TickerSymbol ticker;
    private BigDecimal price;
    private Long count;
    @ManyToOne
    @JoinColumn(name = "commission_id")
    private ExpenseItem commission;
    private BigDecimal commissionAmount;
    @ManyToOne
    @JoinColumn(name = "stock_sale_income_id")
    private IncomeItem stockSaleIncome;
    @ManyToOne
    @JoinColumn(name = "stock_sale_expense_id")
    private ExpenseItem stockSaleExpense;
}