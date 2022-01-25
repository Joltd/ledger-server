package com.evgenltd.ledgerserver.base.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account extends Reference {

    @Builder
    public Account(final Long id, final String name) {
        super(id, name);
    }

}