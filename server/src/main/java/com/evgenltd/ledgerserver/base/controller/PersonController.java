package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.entity.Person;
import com.evgenltd.ledgerserver.base.record.PersonRecord;
import com.evgenltd.ledgerserver.base.record.PersonRow;
import com.evgenltd.ledgerserver.base.service.PersonService;
import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/person")
public class PersonController extends ReferenceController<Person, PersonRecord, PersonRow> {
    public PersonController(final PersonService personService) {
        super(personService);
    }
}