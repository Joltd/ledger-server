package com.evgenltd.ledgerserver.common.controller;

import com.evgenltd.ledgerserver.common.entity.Reference;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class ReferenceController<T extends Reference, D, W> {

    private final ReferenceService<T, D, W> referenceService;

    public ReferenceController(final ReferenceService<T, D, W> referenceService) {
        this.referenceService = referenceService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return referenceService.count(loadConfig);
    }

    @PostMapping("/")
    public List<W> list(@RequestBody final LoadConfig loadConfig) {
        return referenceService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return referenceService.filter(filter);
    }

    @GetMapping("/{id}")
    public D byId(@PathVariable("id") final Long id) {
        return referenceService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final D record) {
        referenceService.update(record);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        referenceService.delete(id);
    }

}
