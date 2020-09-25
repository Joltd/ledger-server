package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.Person;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PersonEditService extends AbstractReferenceEditService<Person> {

    public PersonEditService(
            final BotService botService,
            final PersonRepository personRepository
    ) {
        super(botService, personRepository, Person.class);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

}
