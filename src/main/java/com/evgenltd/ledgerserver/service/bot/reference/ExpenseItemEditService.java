package com.evgenltd.ledgerserver.service.bot.reference;

import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.repository.ExpenseItemRepository;
import com.evgenltd.ledgerserver.service.bot.BotService;
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
