package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.record.AccountRecord;
import com.evgenltd.ledgerserver.base.record.AccountRow;
import com.evgenltd.ledgerserver.base.service.AccountService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return accountService.count(loadConfig);
    }

    @PostMapping("/")
    public List<AccountRow> list(@RequestBody final LoadConfig loadConfig) {
        return accountService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return accountService.filter(filter);
    }

    @GetMapping("/{id}")
    public AccountRecord byId(@PathVariable("id") final Long id) {
        return accountService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final AccountRecord accountRecord) {
        accountService.update(accountRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        accountService.delete(id);
    }

}