package old.ledgerserver.platform.entities.document.buycurrency;

import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import com.evgenltd.ledgerserver.common.entity.Account;
import old.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;

@Entity
@Table(name = "document_buy_currency")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal amount;
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
}