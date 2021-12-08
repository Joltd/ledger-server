package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.Person;
import com.evgenltd.ledgerserver.reference.repository.AccountRepository;
import com.evgenltd.ledgerserver.reference.repository.PersonRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reference/person")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/descriptor/dto")
    public DtoModel dto() {
        return new DtoModel(Arrays.asList(
                new DtoField("id", false, FieldType.NUMBER, null),
                new DtoField("name", false, FieldType.STRING, null)
        ));
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                new MetaField("id", FieldType.NUMBER, null),
                new MetaField("name", FieldType.NUMBER, null)
        ));
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return personRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<Person> load(@RequestBody final LoadConfig loadConfig) {
        return personRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @GetMapping("/{id}")
    public Person byId(@PathVariable("id") final Long id) {
        return personRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
    }

    @PostMapping
    public void update(@RequestBody final Person person) {
        personRepository.save(person);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        personRepository.deleteById(id);
    }

}
