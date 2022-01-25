package com.evgenltd.ledgerserver.common.entity;

import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "journal_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private String code;

    @Enumerated(EnumType.STRING)
    private Type type;

    private BigDecimal amount;

    private String operation;

    // dimensions

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "currency_id")

    private Currency currency;

    private BigDecimal currencyRate;

    private BigDecimal currencyAmount;

    private BigDecimal price;

    private Long count;

    @ManyToOne
    @JoinColumn(name = "expense_item_id")
    private ExpenseItem expenseItem;

    @ManyToOne
    @JoinColumn(name = "income_item_id")
    private IncomeItem incomeItem;

    @ManyToOne
    @JoinColumn(name = "ticker_symbol_id")
    private TickerSymbol tickerSymbol;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private Integer position;

    public BigDecimal amount() {
        return switch (type) {
            case DEBIT -> getAmount();
            case CREDIT -> getAmount().negate();
        };
    }

    public BigDecimal currencyAmount() {
        if (getCurrencyAmount() == null) {
            return BigDecimal.ZERO;
        }
        return switch (type) {
            case DEBIT -> getCurrencyAmount();
            case CREDIT -> getCurrencyAmount().negate();
        };
    }

    public Long count() {
        if (getCount() == null) {
            return 0L;
        }
        return switch (type) {
            case DEBIT -> getCount();
            case CREDIT -> -getCount();
        };
    }

    public enum Type {
        DEBIT,
        CREDIT
    }

}
