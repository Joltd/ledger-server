package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.IncomeItem;
import com.evgenltd.ledgerserver.entity.Person;
import com.evgenltd.ledgerserver.entity.Setting;
import com.evgenltd.ledgerserver.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import com.evgenltd.ledgerserver.repository.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private final PersonRepository personRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final IncomeItemRepository incomeItemRepository;

    public SettingService(
            final SettingRepository settingRepository,
            final PersonRepository personRepository,
            final ExpenseItemRepository expenseItemRepository,
            final IncomeItemRepository incomeItemRepository
    ) {
        this.settingRepository = settingRepository;
        this.personRepository = personRepository;
        this.expenseItemRepository = expenseItemRepository;
        this.incomeItemRepository = incomeItemRepository;
    }

    public Person broker() {
        return asEntity("broker", personRepository::findById);
    }

    public ExpenseItem brokerCommissionExpenseItem() {
        return asEntity("broker.commission.expense_item", expenseItemRepository::findById);
    }

    public Integer brokerCommissionSettlementDay() {
        return as("broker.commission.settlement_day", Setting::asInt);
    }

    public ExpenseItem currencyReassessmentExpenseItem() {
        return asEntity("currency.reassessment.expense_item", expenseItemRepository::findById);
    }

    public IncomeItem currencyReassessmentIncomeItem() {
        return asEntity("currency.reassessment.income_item", incomeItemRepository::findById);
    }

    private <T> T asEntity(final String setting, final Function<Long, Optional<T>> findById) {
        return as(setting, s -> s.asLong().flatMap(findById));
    }

    private <T> T as(final String setting, final Function<Setting, Optional<T>> mapper) {
        return settingRepository.findByName(setting)
                .flatMap(mapper)
                .orElseThrow(() -> new ApplicationException("Unable to read setting [%s]", setting));
    }

}
