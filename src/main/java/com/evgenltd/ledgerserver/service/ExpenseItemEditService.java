package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.repository.ExpenseItemRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ExpenseItemEditService extends AbstractReferenceEditService<ExpenseItem> {

    public ExpenseItemEditService(
            final BotService botService,
            final ExpenseItemRepository expenseItemRepository
    ) {
        super(botService, expenseItemRepository, ExpenseItem.class);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

}
