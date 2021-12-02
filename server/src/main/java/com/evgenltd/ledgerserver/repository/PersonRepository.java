package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
