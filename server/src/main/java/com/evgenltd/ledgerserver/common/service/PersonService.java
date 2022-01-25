package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.common.entity.Person;
import com.evgenltd.ledgerserver.common.record.PersonRecord;
import com.evgenltd.ledgerserver.common.record.PersonRow;
import com.evgenltd.ledgerserver.common.repository.PersonRepository;
import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService extends ReferenceService<Person, PersonRecord, PersonRow> {

    public PersonService(final PersonRepository personRepository) {
        super(personRepository);
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