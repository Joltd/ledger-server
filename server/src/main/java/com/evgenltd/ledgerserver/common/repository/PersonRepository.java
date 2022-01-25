package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.Person;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ReferenceRepository<Person> {}