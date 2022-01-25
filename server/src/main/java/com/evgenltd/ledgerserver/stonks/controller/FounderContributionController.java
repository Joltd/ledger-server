package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.stonks.record.FounderContributionRecord;
import com.evgenltd.ledgerserver.stonks.record.FounderContributionRow;
import com.evgenltd.ledgerserver.stonks.service.FounderContributionService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/founder-contribution")
public class FounderContributionController {

    private final FounderContributionService founderContributionService;

    public FounderContributionController(final FounderContributionService founderContributionService) {
        this.founderContributionService = founderContributionService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return founderContributionService.count(loadConfig);
    }

    @PostMapping("/")
    public List<FounderContributionRow> list(@RequestBody final LoadConfig loadConfig) {
        return founderContributionService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return founderContributionService.filter(filter);
    }

    @GetMapping("/{id}")
    public FounderContributionRecord byId(@PathVariable("id") final Long id) {
        return founderContributionService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final FounderContributionRecord founderContributionRecord) {
        founderContributionService.update(founderContributionRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        founderContributionService.delete(id);
    }

}