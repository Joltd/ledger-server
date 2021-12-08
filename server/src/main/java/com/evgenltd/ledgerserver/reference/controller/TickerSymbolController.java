package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Person;
import com.evgenltd.ledgerserver.reference.entity.TickerSymbol;
import com.evgenltd.ledgerserver.reference.repository.PersonRepository;
import com.evgenltd.ledgerserver.reference.repository.TickerSymbolRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reference/ticker-symbol")
public class TickerSymbolController {

    private final TickerSymbolRepository tickerSymbolRepository;

    public TickerSymbolController(final TickerSymbolRepository tickerSymbolRepository) {
        this.tickerSymbolRepository = tickerSymbolRepository;
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
        return tickerSymbolRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<TickerSymbol> load(@RequestBody final LoadConfig loadConfig) {
        return tickerSymbolRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @GetMapping("/{id}")
    public TickerSymbol byId(@PathVariable("id") final Long id) {
        return tickerSymbolRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
    }

    @PostMapping
    public void update(@RequestBody final TickerSymbol tickerSymbol) {
        tickerSymbolRepository.save(tickerSymbol);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        tickerSymbolRepository.deleteById(id);
    }

}
