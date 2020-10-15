package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import com.evgenltd.ledgerserver.repository.SettingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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

        add(referenceSetting(Settings.BROKER, personRepository));
        add(referenceSetting(Settings.BROKER_COMMISSION_EXPENSE_ITEM, expenseItemRepository));
        add(primitiveSetting(Settings.BROKER_COMMISSION_SETTLEMENT_DAY, Utils::asIntNoThrow, "[1-31]"));
        add(referenceSetting(Settings.CURRENCY_REASSESSMENT_EXPENSE_ITEM, expenseItemRepository));
        add(referenceSetting(Settings.CURRENCY_REASSESSMENT_INCOME_ITEM, incomeItemRepository));
    }

    private void add(final SettingInfo<?> settingInfo) {
        settings.put(settingInfo.name(), settingInfo);
    }

    public List<String> all() {
        return settings.values()
                .stream()
                .sorted(Comparator.comparing(SettingInfo::name))
                .map(settingInfo -> settingInfo.name() + " = " + settingInfo.print())
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

    private <T> SettingInfo<T> primitiveSetting(final String name, final Function<String, Optional<T>> converter, final String example) {
        return new SettingInfo<T>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public T get(final Setting setting) {
                return converter.apply(setting.getValue()).orElse(null);
            }

            @Override
            public void set(final Setting setting, final String value) {
                converter.apply(value).ifPresentOrElse(result -> setting.setValue(value), () -> setting.setValue(null));
            }

            @Override
            public String print() {
                return settingRepository.findByName(name())
                        .flatMap(setting -> converter.apply(setting.getValue()))
                        .map(Object::toString)
                        .orElse(null);
            }

            @Override
            public String example() {
                return example;
            }
        };
    }

    private <T extends Reference> SettingInfo<T> referenceSetting(final String name, final JpaRepository<T, Long> repository) {
        return new SettingInfo<T>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public T get(final Setting setting) {
                return Utils.asLongNoThrow(setting.getValue())
                        .flatMap(repository::findById)
                        .orElse(null);
            }

            @Override
            public void set(final Setting setting, final String value) {
                Utils.asLongNoThrow(value)
                        .flatMap(repository::findById)
                        .ifPresentOrElse(result -> setting.setValue(value), () -> setting.setValue(null));
            }

            @Override
            public String print() {
                return settingRepository.findByName(name())
                        .flatMap(setting -> Utils.asLongNoThrow(setting.getValue()))
                        .flatMap(repository::findById)
                        .map(SettingService::referenceToString)
                        .orElse("");
            }

            @Override
            public String example() {
                return repository.findAll()
                        .stream()
                        .map(SettingService::referenceToString)
                        .collect(Collectors.joining("\n"));
            }
        };
    }

    private Setting of(final String name) {
        final Setting setting = new Setting();
        setting.setName(name);
        settingRepository.save(setting);
        return setting;
    }

    private static String referenceToString(final Reference reference) {
        return String.format("%s | %s", reference.getId(), reference.getName());
    }

    public interface SettingInfo<T> {
        String name();

        T get(Setting setting);

        void set(Setting setting, String value);

        String print();

        String example();
    }
}
