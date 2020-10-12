package com.evgenltd.ledgerserver.entity;

import com.evgenltd.ledgerserver.ApplicationException;

import javax.persistence.*;
import java.util.Optional;
import java.util.function.Function;

@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Optional<Integer> asInt() {
        return as(Integer::parseInt);
    }

    public Optional<Long> asLong() {
        return as(Long::parseLong);
    }

    private <T> Optional<T> as(final Function<String,T> converter) {
        if (getValue() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(converter.apply(getValue()));
        } catch (final NumberFormatException e) {
            throw new ApplicationException("[%s, %s], unable to parse", getName(), getValue());
        }
    }

}
