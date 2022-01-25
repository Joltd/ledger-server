package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRecord;
import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRow;
import com.evgenltd.ledgerserver.stonks.service.TickerSymbolService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/ticker-symbol")
public class TickerSymbolController {

    private final TickerSymbolService tickerSymbolService;

    public TickerSymbolController(final TickerSymbolService tickerSymbolService) {
        this.tickerSymbolService = tickerSymbolService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return tickerSymbolService.count(loadConfig);
    }

    @PostMapping("/")
    public List<TickerSymbolRow> list(@RequestBody final LoadConfig loadConfig) {
        return tickerSymbolService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return tickerSymbolService.filter(filter);
    }

    @GetMapping("/{id}")
    public TickerSymbolRecord byId(@PathVariable("id") final Long id) {
        return tickerSymbolService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final TickerSymbolRecord tickerSymbolRecord) {
        tickerSymbolService.update(tickerSymbolRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        tickerSymbolService.delete(id);
    }

}