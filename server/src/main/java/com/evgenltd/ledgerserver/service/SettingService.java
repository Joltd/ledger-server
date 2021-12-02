package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.builder.SettingInfoBuilder;
import com.evgenltd.ledgerserver.record.SettingInfo;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import com.evgenltd.ledgerserver.repository.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    private final Map<String, SettingInfo<?>> settings = new HashMap<>();

    public SettingService(
            final SettingRepository settingRepository,
            final PersonRepository personRepository,
            final ExpenseItemRepository expenseItemRepository,
            final IncomeItemRepository incomeItemRepository
    ) {
        this.settingRepository = settingRepository;

        add(SettingInfoBuilder.referenceSetting(Settings.BROKER, personRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.BROKER_COMMISSION_EXPENSE_ITEM, expenseItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.CURRENCY_REASSESSMENT_EXPENSE_ITEM, expenseItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.CURRENCY_REASSESSMENT_INCOME_ITEM, incomeItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.CURRENCY_SALE_EXPENSE_ITEM, expenseItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.CURRENCY_SALE_INCOME_ITEM, incomeItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.STOCK_REASSESSMENT_EXPENSE_ITEM, expenseItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.STOCK_REASSESSMENT_INCOME_ITEM, incomeItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.STOCK_SALE_EXPENSE_ITEM, expenseItemRepository));
        add(SettingInfoBuilder.referenceSetting(Settings.STOCK_SALE_INCOME_ITEM, incomeItemRepository));
    }

    private void add(final SettingInfo<?> settingInfo) {
        settings.put(settingInfo.name(), settingInfo);
    }

    public List<String> all() {
        return settings.values()
                .stream()
                .sorted(Comparator.comparing(SettingInfo::name))
                .map(settingInfo -> settingInfo.name() + " = " + settingRepository.findByName(settingInfo.name()).map(settingInfo::print).orElse(""))
                .collect(Collectors.toList());
    }

    public boolean has(final String setting) {
        return settings.containsKey(setting);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String name) {
        final Setting setting = settingRepository.findByName(name)
                .orElseGet(() -> of(name));
        final SettingInfo<?> settingInfo = settings.get(name);
        return (T) settingInfo.get(setting);
    }

    public void set(final String name, final String value) {
        final Setting setting = settingRepository.findByName(name)
                .orElseGet(() -> of(name));
        final SettingInfo<?> settingInfo = settings.get(name);
        settingInfo.set(setting, value);
        settingRepository.save(setting);
    }

    public String example(final String name) {
        final SettingInfo<?> settingInfo = settings.get(name);
        return settingInfo != null
                ? settingInfo.example()
                : "";
    }

    private Setting of(final String name) {
        final Setting setting = new Setting();
        setting.setName(name);
        settingRepository.save(setting);
        return setting;
    }

}
