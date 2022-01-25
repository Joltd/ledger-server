package com.evgenltd.ledgerserver.common.controller;

import com.evgenltd.ledgerserver.common.record.ExpenseItemRecord;
import com.evgenltd.ledgerserver.common.record.ExpenseItemRow;
import com.evgenltd.ledgerserver.common.service.ExpenseItemService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/expense-item")
public class ExpenseItemController {

    private final ExpenseItemService expenseItemService;

    public ExpenseItemController(final ExpenseItemService expenseItemService) {
        this.expenseItemService = expenseItemService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return expenseItemService.count(loadConfig);
    }

    @PostMapping("/")
    public List<ExpenseItemRow> list(@RequestBody final LoadConfig loadConfig) {
        return expenseItemService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return expenseItemService.filter(filter);
    }

    @GetMapping("/{id}")
    public ExpenseItemRecord byId(@PathVariable("id") final Long id) {
        return expenseItemService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final ExpenseItemRecord expenseItemRecord) {
        expenseItemService.update(expenseItemRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        expenseItemService.delete(id);
    }

}