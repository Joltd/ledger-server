package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.base.record.IncomeItemRecord;
import com.evgenltd.ledgerserver.base.record.IncomeItemRow;
import com.evgenltd.ledgerserver.base.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class IncomeItemService extends ReferenceService<IncomeItem, IncomeItemRecord, IncomeItemRow> {

    public IncomeItemService(
            final SettingService settingService,
            final IncomeItemRepository incomeItemRepository
    ) {
        super(settingService, incomeItemRepository);
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