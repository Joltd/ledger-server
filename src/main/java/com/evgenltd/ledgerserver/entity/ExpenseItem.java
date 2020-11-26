package com.evgenltd.ledgerserver.entity;

import javax.persistence.*;

@Entity
@Table(name = "expense_items")
public class ExpenseItem implements Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", id, name);
    }

}
