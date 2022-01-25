package com.evgenltd.ledgerserver.base.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "expense_items")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseItem extends Reference {

    @Builder
    public ExpenseItem(final Long id, final String name) {
        super(id, name);
    }

}