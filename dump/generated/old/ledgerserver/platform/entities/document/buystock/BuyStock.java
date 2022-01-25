package old.ledgerserver.platform.entities.document.buystock;

import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;

import java.lang.Long;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;

@Entity
@Table(name = "document_buy_stock")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal amount;
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
}