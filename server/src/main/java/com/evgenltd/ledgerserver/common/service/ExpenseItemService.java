package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.record.ExpenseItemRecord;
import com.evgenltd.ledgerserver.common.record.ExpenseItemRow;
import com.evgenltd.ledgerserver.common.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseItemService extends ReferenceService<ExpenseItem, ExpenseItemRecord, ExpenseItemRow> {

    public ExpenseItemService(final ExpenseItemRepository expenseItemRepository) {
        super(expenseItemRepository);
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