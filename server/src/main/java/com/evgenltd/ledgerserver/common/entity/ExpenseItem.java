package com.evgenltd.ledgerserver.common.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "expense_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseItem extends Reference {}