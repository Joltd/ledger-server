package com.evgenltd.ledgerserver.common.controller;

import com.evgenltd.ledgerserver.common.record.CurrencyRecord;
import com.evgenltd.ledgerserver.common.record.CurrencyRow;
import com.evgenltd.ledgerserver.common.service.CurrencyService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(final CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return currencyService.count(loadConfig);
    }

    @PostMapping("/")
    public List<CurrencyRow> list(@RequestBody final LoadConfig loadConfig) {
        return currencyService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return currencyService.filter(filter);
    }

    @GetMapping("/{id}")
    public CurrencyRecord byId(@PathVariable("id") final Long id) {
        return currencyService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final CurrencyRecord currencyRecord) {
        currencyService.update(currencyRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        currencyService.delete(id);
    }

}