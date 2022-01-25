package com.evgenltd.ledgerserver.common.controller;

import com.evgenltd.ledgerserver.common.record.IncomeItemRecord;
import com.evgenltd.ledgerserver.common.record.IncomeItemRow;
import com.evgenltd.ledgerserver.common.service.IncomeItemService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/income-item")
public class IncomeItemController {

    private final IncomeItemService incomeItemService;

    public IncomeItemController(final IncomeItemService incomeItemService) {
        this.incomeItemService = incomeItemService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return incomeItemService.count(loadConfig);
    }

    @PostMapping("/")
    public List<IncomeItemRow> list(@RequestBody final LoadConfig loadConfig) {
        return incomeItemService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return incomeItemService.filter(filter);
    }

    @GetMapping("/{id}")
    public IncomeItemRecord byId(@PathVariable("id") final Long id) {
        return incomeItemService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final IncomeItemRecord incomeItemRecord) {
        incomeItemService.update(incomeItemRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        incomeItemService.delete(id);
    }

}