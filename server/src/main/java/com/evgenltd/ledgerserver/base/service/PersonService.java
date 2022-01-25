package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.entity.Person;
import com.evgenltd.ledgerserver.base.record.PersonRecord;
import com.evgenltd.ledgerserver.base.record.PersonRow;
import com.evgenltd.ledgerserver.base.repository.PersonRepository;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends ReferenceService<Person, PersonRecord, PersonRow> {

    public PersonService(
            final SettingService settingService,
            final PersonRepository personRepository
    ) {
        super(settingService, personRepository);
    }

    @Override
    protected PersonRow toRow(final Person person) {
        return new PersonRow(
            person.getId(),
            person.getName()
        );
    }

    @Override
    protected PersonRecord toRecord(final Person person) {
        return new PersonRecord(
            person.getId(),
            person.getName()
        );
    }

    @Override
    protected Person toEntity(final PersonRecord personRecord) {
        final Person person = new Person();
        person.setId(personRecord.id());
        person.setName(personRecord.name());
        return person;
    }

}