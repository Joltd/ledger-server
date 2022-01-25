package com.evgenltd.ledgerserver.base.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
public class Currency extends Reference {

    @Builder
    public Currency(final Long id, final String name) {
        super(id, name);
    }

}
