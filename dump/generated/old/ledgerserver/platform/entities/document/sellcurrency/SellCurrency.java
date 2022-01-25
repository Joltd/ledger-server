package old.ledgerserver.platform.entities.document.sellcurrency;

import lombok.*;
import javax.persistence.*;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import old.ledgerserver.platform.entities.reference.currency.Currency;
import java.math.BigDecimal;

import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;

@Entity
@Table(name = "document_sell_currency")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
    private BigDecimal currencyRate;
    private BigDecimal currencyAmount;
    @ManyToOne
    @JoinColumn(name = "commission_id")
    private ExpenseItem commission;
    private BigDecimal commissionAmount;
    @ManyToOne
    @JoinColumn(name = "currency_sale_income_id")
    private IncomeItem currencySaleIncome;
    @ManyToOne
    @JoinColumn(name = "currency_sale_expense_id")
    private ExpenseItem currencySaleExpense;
}