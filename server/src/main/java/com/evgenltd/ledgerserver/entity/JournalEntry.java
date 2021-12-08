package com.evgenltd.ledgerserver.entity;

import com.evgenltd.ledgerserver.reference.entity.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
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

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(final LocalDateTime date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {
        this.account = account;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(final BigDecimal currencyRate) {
        this.currencyRate = currencyRate;
    }

    public BigDecimal getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(final BigDecimal currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(final Long count) {
        this.count = count;
    }

    public ExpenseItem getExpenseItem() {
        return expenseItem;
    }

    public void setExpenseItem(final ExpenseItem expenseItem) {
        this.expenseItem = expenseItem;
    }

    public IncomeItem getIncomeItem() {
        return incomeItem;
    }

    public void setIncomeItem(final IncomeItem incomeItem) {
        this.incomeItem = incomeItem;
    }

    public TickerSymbol getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(final TickerSymbol tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(final Document document) {
        this.document = document;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(final ProductType productType) {
        this.productType = productType;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
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
