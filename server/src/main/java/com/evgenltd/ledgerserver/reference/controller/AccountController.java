package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.repository.AccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reference/account")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
        return accountRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<Account> load(@RequestBody final LoadConfig loadConfig) {
        return accountRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @GetMapping("/{id}")
    public Account byId(@PathVariable("id") final Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
    }

    @PostMapping
    public void update(@RequestBody final Account account) {
        accountRepository.save(account);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        accountRepository.deleteById(id);
    }

}
