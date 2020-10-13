package com.evgenltd.ledgerserver.service.bot.reference;

import com.evgenltd.ledgerserver.entity.Person;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import com.evgenltd.ledgerserver.service.bot.BotService;
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
