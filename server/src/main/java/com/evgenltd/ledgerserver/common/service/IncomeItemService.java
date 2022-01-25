package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.common.entity.IncomeItem;
import com.evgenltd.ledgerserver.common.record.IncomeItemRecord;
import com.evgenltd.ledgerserver.common.record.IncomeItemRow;
import com.evgenltd.ledgerserver.common.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeItemService extends ReferenceService<IncomeItem, IncomeItemRecord, IncomeItemRow> {

    public IncomeItemService(final IncomeItemRepository incomeItemRepository) {
        super(incomeItemRepository);
    }

    @Override
    protected IncomeItemRow toRow(final IncomeItem incomeItem) {
        return new IncomeItemRow(
            incomeItem.getId(),
            incomeItem.getName()
        );
    }

    @Override
    protected IncomeItemRecord toRecord(final IncomeItem incomeItem) {
        return new IncomeItemRecord(
            incomeItem.getId(),
            incomeItem.getName()
        );
    }

    @Override
    protected IncomeItem toEntity(final IncomeItemRecord incomeItemRecord) {
        final IncomeItem incomeItem = new IncomeItem();
        incomeItem.setId(incomeItemRecord.id());
        incomeItem.setName(incomeItemRecord.name());
        return incomeItem;
    }

}