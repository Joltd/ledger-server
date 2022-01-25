package com.evgenltd.ledgerserver.stonks.service;

import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRecord;
import com.evgenltd.ledgerserver.stonks.record.TickerSymbolRow;
import com.evgenltd.ledgerserver.stonks.repository.TickerSymbolRepository;
import org.springframework.stereotype.Service;

@Service
public class TickerSymbolService extends ReferenceService<TickerSymbol, TickerSymbolRecord, TickerSymbolRow> {

    public TickerSymbolService(final TickerSymbolRepository tickerSymbolRepository) {
        super(tickerSymbolRepository);
    }

    @Override
    protected TickerSymbolRow toRow(final TickerSymbol tickerSymbol) {
        return new TickerSymbolRow(
            tickerSymbol.getId(),
            tickerSymbol.getName(),
            tickerSymbol.getFigi(),
            tickerSymbol.getWithoutCommission()
        );
    }

    @Override
    protected TickerSymbolRecord toRecord(final TickerSymbol tickerSymbol) {
        return new TickerSymbolRecord(
            tickerSymbol.getId(),
            tickerSymbol.getName(),
            tickerSymbol.getFigi(),
            tickerSymbol.getWithoutCommission()
        );
    }

    @Override
    protected TickerSymbol toEntity(final TickerSymbolRecord tickerSymbolRecord) {
        final TickerSymbol tickerSymbol = new TickerSymbol();
        tickerSymbol.setId(tickerSymbolRecord.id());
        tickerSymbol.setName(tickerSymbolRecord.name());
        tickerSymbol.setFigi(tickerSymbolRecord.figi());
        tickerSymbol.setWithoutCommission(tickerSymbolRecord.withoutCommission());
        return tickerSymbol;
    }

}