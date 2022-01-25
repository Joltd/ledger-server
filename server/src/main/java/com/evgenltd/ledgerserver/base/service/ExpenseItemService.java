package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.base.record.ExpenseItemRecord;
import com.evgenltd.ledgerserver.base.record.ExpenseItemRow;
import com.evgenltd.ledgerserver.base.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class ExpenseItemService extends ReferenceService<ExpenseItem, ExpenseItemRecord, ExpenseItemRow> {

    public ExpenseItemService(
            final SettingService settingService,
            final ExpenseItemRepository expenseItemRepository
    ) {
        super(settingService, expenseItemRepository);
    }

    @Override
    protected ExpenseItemRow toRow(final ExpenseItem expenseItem) {
        return new ExpenseItemRow(
            expenseItem.getId(),
            expenseItem.getName()
        );
    }

    @Override
    protected  ExpenseItemRecord toRecord(final ExpenseItem expenseItem) {
        return new ExpenseItemRecord(
            expenseItem.getId(),
            expenseItem.getName()
        );
    }

    @Override
    protected  ExpenseItem toEntity(final ExpenseItemRecord expenseItemRecord) {
        final ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setId(expenseItemRecord.id());
        expenseItem.setName(expenseItemRecord.name());
        return expenseItem;
    }

}