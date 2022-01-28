package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRecord;
import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRow;
import com.evgenltd.ledgerserver.stonks.service.TickerSymbolService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/ticker-symbol")
public class TickerSymbolController extends ReferenceController<TickerSymbol, TickerSymbolRecord, TickerSymbolRow> {
    public TickerSymbolController(final TickerSymbolService tickerSymbolService) {
        super(tickerSymbolService);
    }
}