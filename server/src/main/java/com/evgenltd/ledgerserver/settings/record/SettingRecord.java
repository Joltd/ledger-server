package com.evgenltd.ledgerserver.settings.record;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

public record SettingRecord(
        Long id,
        ReferenceRecord broker,
        ReferenceRecord brokerCommissionExpenseItem,
        ReferenceRecord currencyReassessmentExpenseItem,
        ReferenceRecord currencyReassessmentIncomeItem,
        ReferenceRecord currencySaleExpenseItem,
        ReferenceRecord currencySaleIncomeItem,
        ReferenceRecord stockReassessmentExpenseItem,
        ReferenceRecord stockReassessmentIncomeItem,
        ReferenceRecord stockSaleExpenseItem,
        ReferenceRecord stockSaleIncomeItem
) {}
