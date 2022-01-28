package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.entity.Currency;
import com.evgenltd.ledgerserver.base.record.CurrencyRecord;
import com.evgenltd.ledgerserver.base.record.CurrencyRow;
import com.evgenltd.ledgerserver.base.service.CurrencyService;
import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/currency")
public class CurrencyController extends ReferenceController<Currency, CurrencyRecord, CurrencyRow> {
    public CurrencyController(final CurrencyService currencyService) {
        super(currencyService);
    }
}