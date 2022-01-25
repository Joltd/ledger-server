package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.record.PersonRecord;
import com.evgenltd.ledgerserver.base.record.PersonRow;
import com.evgenltd.ledgerserver.base.service.PersonService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(final PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return personService.count(loadConfig);
    }

    @PostMapping("/")
    public List<PersonRow> list(@RequestBody final LoadConfig loadConfig) {
        return personService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return personService.filter(filter);
    }

    @GetMapping("/{id}")
    public PersonRecord byId(@PathVariable("id") final Long id) {
        return personService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final PersonRecord personRecord) {
        personService.update(personRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        personService.delete(id);
    }

}