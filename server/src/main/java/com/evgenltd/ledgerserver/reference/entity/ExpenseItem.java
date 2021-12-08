package com.evgenltd.ledgerserver.reference.entity;

import com.evgenltd.ledgerserver.entity.Reference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "expense_items")
@Getter
@Setter
@ToString
public class ExpenseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}
