package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.TickerSymbolRepository;
import com.evgenltd.ledgerserver.service.DataService;
import com.evgenltd.ledgerserver.service.brocker.tinkoff.TinkoffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class DataController {

    private final TinkoffService tinkoffService;
    private final TickerSymbolRepository tickerSymbolRepository;
    private final DataService dataService;

    public DataController(
            final TinkoffService tinkoffService,
            final TickerSymbolRepository tickerSymbolRepository,
            final DataService dataService
    ) {
        this.tinkoffService = tinkoffService;
        this.tickerSymbolRepository = tickerSymbolRepository;
        this.dataService = dataService;
    }

    @GetMapping("/price/{instrument}")
    public BigDecimal price(@PathVariable("instrument") final String instrument) {
        final TickerSymbol ticker = tickerSymbolRepository.findByName(instrument.toUpperCase());
        if (ticker != null) {
            return tinkoffService.price(ticker.getFigi());
        }

        return tinkoffService.price(Currency.valueOf(instrument.toUpperCase()).getFigi());
    }

    @GetMapping("/portfolio")
    public List<DataService.PortfolioEntry> portfolio() {
        return dataService.portfolio();
    }

}
