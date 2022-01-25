package old.ledgerserver.platform.entities.document.buycurrencystock;

import lombok.*;
import javax.persistence.*;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import java.math.BigDecimal;
import java.lang.Long;
import old.ledgerserver.platform.entities.reference.currency.Currency;

@Entity
@Table(name = "document_buy_currency_stock")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyCurrencyStock {
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
    @JoinColumn(name = "currency_id")
    private Currency currency;
    private BigDecimal currencyAmount;
}