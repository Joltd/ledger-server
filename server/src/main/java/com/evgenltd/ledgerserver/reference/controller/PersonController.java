package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.Person;
import com.evgenltd.ledgerserver.reference.repository.AccountRepository;
import com.evgenltd.ledgerserver.reference.repository.PersonRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                DtoField.builder().reference("id").type(FieldType.NUMBER).build(),
                DtoField.builder().reference("name").type(FieldType.STRING).build()
        ));
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                MetaField.builder().reference("id").type(FieldType.NUMBER).build(),
                MetaField.builder().reference("name").type(FieldType.NUMBER).build()
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

    @GetMapping("/")
    public List<Person> filter(@QueryParam("filter") final String filter) {
        return personRepository.findAll()
                .stream()
                .filter(person -> StringUtils.isBlank(filter) || person.getName().contains(filter))
                .collect(Collectors.toList());
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
