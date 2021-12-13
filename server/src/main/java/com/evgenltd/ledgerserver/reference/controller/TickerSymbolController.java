package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.Person;
import com.evgenltd.ledgerserver.reference.entity.TickerSymbol;
import com.evgenltd.ledgerserver.reference.repository.PersonRepository;
import com.evgenltd.ledgerserver.reference.repository.TickerSymbolRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                DtoField.builder().reference("id").type(FieldType.NUMBER).build(),
                DtoField.builder().reference("name").type(FieldType.STRING).build()
        ));
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                MetaField.builder().reference("id").type(FieldType.NUMBER).build(),
                MetaField.builder().reference("name").type(FieldType.NUMBER).build()
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

    @GetMapping("/")
    public List<TickerSymbol> filter(@QueryParam("filter") final String filter) {
        return tickerSymbolRepository.findAll()
                .stream()
                .filter(person -> StringUtils.isBlank(filter) || person.getName().contains(filter))
                .collect(Collectors.toList());
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
