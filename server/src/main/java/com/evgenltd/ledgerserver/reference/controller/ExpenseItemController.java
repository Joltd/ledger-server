package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.ExpenseItem;
import com.evgenltd.ledgerserver.reference.repository.ExpenseItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reference/expense-item")
public class ExpenseItemController {

    private final ExpenseItemRepository expenseItemRepository;

    public ExpenseItemController(final ExpenseItemRepository expenseItemRepository) {
        this.expenseItemRepository = expenseItemRepository;
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
        return expenseItemRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<ExpenseItem> load(@RequestBody final LoadConfig loadConfig) {
        return expenseItemRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @GetMapping("/{id}")
    public ExpenseItem byId(@PathVariable("id") final Long id) {
        return expenseItemRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
    }

    @PostMapping
    public void update(@RequestBody final ExpenseItem expenseItem) {
        expenseItemRepository.save(expenseItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        expenseItemRepository.deleteById(id);
    }

}
