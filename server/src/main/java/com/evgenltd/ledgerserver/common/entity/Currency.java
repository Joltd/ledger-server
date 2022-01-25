package com.evgenltd.ledgerserver.common.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency extends Reference {}
