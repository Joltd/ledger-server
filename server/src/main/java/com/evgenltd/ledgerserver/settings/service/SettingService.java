package com.evgenltd.ledgerserver.settings.service;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.base.entity.Person;
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
                .orElse(null);
    }

    public SettingRecord loadRecord() {
        return settingRepository.findAll()
                .stream()
                .findFirst()
                .map(this::toRecord)
                .orElse(null);
    }

    public void update(final SettingRecord settingRecord) {
        final Setting setting = toEntity(settingRecord);
        settingRepository.save(setting);
    }

    private SettingRecord toRecord(final Setting entity) {
        return new SettingRecord(
                entity.getId(),
                new ReferenceRecord(entity.getBroker().getId(), entity.getBroker().getName()),
                new ReferenceRecord(entity.getBrokerCommissionExpenseItem().getId(), entity.getBrokerCommissionExpenseItem().getName()),
                new ReferenceRecord(entity.getCurrencyReassessmentExpenseItem().getId(), entity.getCurrencyReassessmentExpenseItem().getName()),
                new ReferenceRecord(entity.getCurrencyReassessmentIncomeItem().getId(), entity.getCurrencyReassessmentIncomeItem().getName()),
                new ReferenceRecord(entity.getCurrencySaleExpenseItem().getId(), entity.getCurrencySaleExpenseItem().getName()),
                new ReferenceRecord(entity.getCurrencySaleIncomeItem().getId(), entity.getCurrencySaleIncomeItem().getName()),
                new ReferenceRecord(entity.getStockReassessmentExpenseItem().getId(), entity.getStockReassessmentExpenseItem().getName()),
                new ReferenceRecord(entity.getStockReassessmentIncomeItem().getId(), entity.getStockReassessmentIncomeItem().getName()),
                new ReferenceRecord(entity.getStockSaleExpenseItem().getId(), entity.getStockSaleExpenseItem().getName()),
                new ReferenceRecord(entity.getStockSaleIncomeItem().getId(), entity.getStockSaleIncomeItem().getName())
        );
    }

    private Setting toEntity(final SettingRecord record) {
        final Setting setting = new Setting();
        setting.setId(record.id());
        setting.setBroker(Person.builder().id(record.broker().id()).name(record.broker().name()).build());
        setting.setBrokerCommissionExpenseItem(ExpenseItem.builder().id(record.brokerCommissionExpenseItem().id()).name(record.brokerCommissionExpenseItem().name()).build());
        setting.setCurrencyReassessmentExpenseItem(ExpenseItem.builder().id(record.currencyReassessmentExpenseItem().id()).name(record.currencyReassessmentExpenseItem().name()).build());
        setting.setCurrencyReassessmentIncomeItem(IncomeItem.builder().id(record.currencyReassessmentIncomeItem().id()).name(record.currencyReassessmentIncomeItem().name()).build());
        setting.setCurrencySaleExpenseItem(ExpenseItem.builder().id(record.currencySaleExpenseItem().id()).name(record.currencySaleExpenseItem().name()).build());
        setting.setCurrencySaleIncomeItem(IncomeItem.builder().id(record.currencySaleIncomeItem().id()).name(record.currencySaleIncomeItem().name()).build());
        setting.setStockReassessmentExpenseItem(ExpenseItem.builder().id(record.stockReassessmentExpenseItem().id()).name(record.stockReassessmentExpenseItem().name()).build());
        setting.setStockReassessmentIncomeItem(IncomeItem.builder().id(record.stockReassessmentIncomeItem().id()).name(record.stockReassessmentIncomeItem().name()).build());
        setting.setStockSaleExpenseItem(ExpenseItem.builder().id(record.stockSaleExpenseItem().id()).name(record.stockSaleExpenseItem().name()).build());
        setting.setStockSaleIncomeItem(IncomeItem.builder().id(record.stockSaleIncomeItem().id()).name(record.stockSaleIncomeItem().name()).build());
        return setting;
    }

}
