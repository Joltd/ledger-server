package com.evgenltd.ledgerserver.reference.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "ticker_symbols")
@Getter
@Setter
@ToString
public class TickerSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String figi;
    private Boolean withoutCommission;

}
