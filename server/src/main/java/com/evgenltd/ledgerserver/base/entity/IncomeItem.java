package com.evgenltd.ledgerserver.base.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "income_items")
@Getter
@Setter
@NoArgsConstructor
public class IncomeItem extends Reference {

    @Builder
    public IncomeItem(final Long id, final String name) {
        super(id, name);
    }

}