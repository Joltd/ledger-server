package com.evgenltd.ledgerserver.base.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
public class Person extends Reference {

    @Builder
    public Person(final Long id, final String name) {
        super(id, name);
    }

}