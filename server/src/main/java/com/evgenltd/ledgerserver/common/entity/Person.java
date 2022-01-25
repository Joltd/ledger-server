package com.evgenltd.ledgerserver.common.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "persons")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person extends Reference {}