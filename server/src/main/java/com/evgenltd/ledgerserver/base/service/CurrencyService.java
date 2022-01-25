package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.entity.Currency;
import com.evgenltd.ledgerserver.base.record.CurrencyRecord;
import com.evgenltd.ledgerserver.base.record.CurrencyRow;
import com.evgenltd.ledgerserver.base.repository.CurrencyRepository;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService extends ReferenceService<Currency, CurrencyRecord, CurrencyRow> {

    public CurrencyService(
            final SettingService settingService,
            final CurrencyRepository currencyRepository
    ) {
        super(settingService, currencyRepository);
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