package com.evgenltd.ledgerserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean enabled;

    private Integer length;

    private BigDecimal amount;

    private LocalDateTime time;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

}
