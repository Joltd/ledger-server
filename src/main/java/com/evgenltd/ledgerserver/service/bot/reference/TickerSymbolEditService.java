package com.evgenltd.ledgerserver.service.bot.reference;

import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.TickerSymbolRepository;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TickerSymbolEditService extends AbstractReferenceEditService<TickerSymbol> {

    public TickerSymbolEditService(
            final BotService botService,
            final TickerSymbolRepository tickerSymbolRepository
    ) {
        super(botService, tickerSymbolRepository, TickerSymbol.class);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

}
