package com.evgenltd.ledgerserver.entity;

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

    // dimensions

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal currencyRate;

    private BigDecimal currencyAmount;

    private Long count;

    @ManyToOne
    @JoinColumn(name = "ticker_symbol_id")
    private TickerSymbol tickerSymbol;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    enum Type {
        DEBIT,
        CREDIT
    }

}
