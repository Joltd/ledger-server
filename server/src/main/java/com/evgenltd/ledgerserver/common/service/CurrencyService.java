package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.common.entity.Currency;
import com.evgenltd.ledgerserver.common.record.CurrencyRecord;
import com.evgenltd.ledgerserver.common.record.CurrencyRow;
import com.evgenltd.ledgerserver.common.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService extends ReferenceService<Currency, CurrencyRecord, CurrencyRow> {

    public CurrencyService(final CurrencyRepository currencyRepository) {
        super(currencyRepository);
    }

    @Override
    protected CurrencyRow toRow(final Currency currency) {
        return new CurrencyRow(
            currency.getId(),
            currency.getName()
        );
    }

    @Override
    protected CurrencyRecord toRecord(final Currency currency) {
        return new CurrencyRecord(
            currency.getId(),
            currency.getName()
        );
    }

    @Override
    protected Currency toEntity(final CurrencyRecord currencyRecord) {
        final Currency currency = new Currency();
        currency.setId(currencyRecord.id());
        currency.setName(currencyRecord.name());
        return currency;
    }

}