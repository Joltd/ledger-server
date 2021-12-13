package com.evgenltd.ledgerserver.document.common.entity;

import com.evgenltd.ledgerserver.reference.entity.*;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
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
    @Enumerated(EnumType.STRING)
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

    public void setAccountId(Long id) {
        final Account account = new Account();
        account.setId(id);
        setAccount(account);
    }

    public void setPersonId(Long id) {
        final Person person = new Person();
        person.setId(id);
        setPerson(person);
    }

    public void setExpenseItemId(Long id) {
        final ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setId(id);
        setExpenseItem(expenseItem);
    }

    public void setIncomeItemId(Long id) {
        final IncomeItem incomeItem = new IncomeItem();
        incomeItem.setId(id);
        setIncomeItem(incomeItem);
    }

    public void setTickerSymbolId(Long id) {
        final TickerSymbol tickerSymbol = new TickerSymbol();
        tickerSymbol.setId(id);
        setTickerSymbol(tickerSymbol);
    }


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
