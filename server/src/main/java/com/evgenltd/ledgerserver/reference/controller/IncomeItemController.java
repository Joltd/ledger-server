package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.ExpenseItem;
import com.evgenltd.ledgerserver.reference.entity.IncomeItem;
import com.evgenltd.ledgerserver.reference.repository.IncomeItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reference/income-item")
public class IncomeItemController {

    private final IncomeItemRepository incomeItemRepository;

    public IncomeItemController(final IncomeItemRepository incomeItemRepository) {
        this.incomeItemRepository = incomeItemRepository;
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
        return incomeItemRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<IncomeItem> load(@RequestBody final LoadConfig loadConfig) {
        return incomeItemRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @GetMapping("/{id}")
    public IncomeItem byId(@PathVariable("id") final Long id) {
        return incomeItemRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
    }

    @PostMapping
    public void update(@RequestBody final IncomeItem incomeItem) {
        incomeItemRepository.save(incomeItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        incomeItemRepository.deleteById(id);
    }

}
