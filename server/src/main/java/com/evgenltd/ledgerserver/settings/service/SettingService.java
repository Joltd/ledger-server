package com.evgenltd.ledgerserver.settings.service;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.base.entity.Person;
import com.evgenltd.ledgerserver.common.entity.Reference;
import com.evgenltd.ledgerserver.settings.entity.Setting;
import com.evgenltd.ledgerserver.settings.record.SettingRecord;
import com.evgenltd.ledgerserver.settings.repository.SettingRepository;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(final SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public Setting load() {
        return settingRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new Setting());
    }

    public SettingRecord loadRecord() {
        final Setting setting = load();
        return toRecord(setting);
    }

    public void update(final SettingRecord settingRecord) {
        final Setting setting = toEntity(settingRecord);
        settingRepository.save(setting);
    }

    private SettingRecord toRecord(final Setting entity) {
        return new SettingRecord(
                entity.getId(),
                toReferenceRecord(entity.getBroker()),
                toReferenceRecord(entity.getBrokerCommissionExpenseItem()),
                toReferenceRecord(entity.getCurrencyReassessmentExpenseItem()),
                toReferenceRecord(entity.getCurrencyReassessmentIncomeItem()),
                toReferenceRecord(entity.getCurrencySaleExpenseItem()),
                toReferenceRecord(entity.getCurrencySaleIncomeItem()),
                toReferenceRecord(entity.getStockReassessmentExpenseItem()),
                toReferenceRecord(entity.getStockReassessmentIncomeItem()),
                toReferenceRecord(entity.getStockSaleExpenseItem()),
                toReferenceRecord(entity.getStockSaleIncomeItem())
        );
    }

    private ReferenceRecord toReferenceRecord(final Reference reference) {
        return reference == null
                ? null
                : new ReferenceRecord(reference.getId(), reference.getName());
    }

    private Setting toEntity(final SettingRecord record) {
        final Setting setting = new Setting();
        setting.setId(record.id());
        if (record.broker() != null) {
            setting.setBroker(Person.builder().id(record.broker().id()).name(record.broker().name()).build());
        }
        if (record.brokerCommissionExpenseItem() != null) {
            setting.setBrokerCommissionExpenseItem(ExpenseItem.builder()
                    .id(record.brokerCommissionExpenseItem().id())
                    .name(record.brokerCommissionExpenseItem().name())
                    .build());
        }
        if (record.currencyReassessmentExpenseItem() != null) {
            setting.setCurrencyReassessmentExpenseItem(ExpenseItem.builder()
                    .id(record.currencyReassessmentExpenseItem().id())
                    .name(record.currencyReassessmentExpenseItem().name())
                    .build());
        }
        if (record.currencyReassessmentIncomeItem() != null) {
            setting.setCurrencyReassessmentIncomeItem(IncomeItem.builder()
                    .id(record.currencyReassessmentIncomeItem().id())
                    .name(record.currencyReassessmentIncomeItem().name())
                    .build());
        }
        if (record.currencySaleExpenseItem() != null) {
            setting.setCurrencySaleExpenseItem(ExpenseItem.builder()
                    .id(record.currencySaleExpenseItem().id())
                    .name(record.currencySaleExpenseItem().name())
                    .build());
        }
        if (record.currencySaleIncomeItem() != null) {
            setting.setCurrencySaleIncomeItem(IncomeItem.builder()
                    .id(record.currencySaleIncomeItem().id())
                    .name(record.currencySaleIncomeItem().name())
                    .build());
        }
        if (record.stockReassessmentExpenseItem() != null) {
            setting.setStockReassessmentExpenseItem(ExpenseItem.builder()
                    .id(record.stockReassessmentExpenseItem().id())
                    .name(record.stockReassessmentExpenseItem().name())
                    .build());
        }
        if (record.stockReassessmentIncomeItem() != null) {
            setting.setStockReassessmentIncomeItem(IncomeItem.builder()
                    .id(record.stockReassessmentIncomeItem().id())
                    .name(record.stockReassessmentIncomeItem().name())
                    .build());
        }
        if (record.stockSaleExpenseItem() != null) {
            setting.setStockSaleExpenseItem(ExpenseItem.builder()
                    .id(record.stockSaleExpenseItem().id())
                    .name(record.stockSaleExpenseItem().name())
                    .build());
        }
        if (record.stockSaleIncomeItem() != null) {
            setting.setStockSaleIncomeItem(IncomeItem.builder()
                    .id(record.stockSaleIncomeItem().id())
                    .name(record.stockSaleIncomeItem().name())
                    .build());
        }
        return setting;
    }



}
