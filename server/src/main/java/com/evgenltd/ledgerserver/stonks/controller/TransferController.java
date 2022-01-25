package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.stonks.record.TransferRecord;
import com.evgenltd.ledgerserver.stonks.record.TransferRow;
import com.evgenltd.ledgerserver.stonks.service.TransferService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(final TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return transferService.count(loadConfig);
    }

    @PostMapping("/")
    public List<TransferRow> list(@RequestBody final LoadConfig loadConfig) {
        return transferService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return transferService.filter(filter);
    }

    @GetMapping("/{id}")
    public TransferRecord byId(@PathVariable("id") final Long id) {
        return transferService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final TransferRecord transferRecord) {
        transferService.update(transferRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        transferService.delete(id);
    }

}