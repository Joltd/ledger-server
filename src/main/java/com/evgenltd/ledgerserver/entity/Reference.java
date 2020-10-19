package com.evgenltd.ledgerserver.entity;

public interface Reference {

    Long getId();

    void setId(final Long id);

    String getName();

    void setName(final String name);

    default String asString() {
        return String.format("%s | %s", getId(), getName());
    }

}
