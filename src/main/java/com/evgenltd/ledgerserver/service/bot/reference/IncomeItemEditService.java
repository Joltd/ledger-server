package com.evgenltd.ledgerserver.service.bot.reference;

import com.evgenltd.ledgerserver.entity.IncomeItem;
import com.evgenltd.ledgerserver.repository.IncomeItemRepository;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class IncomeItemEditService extends AbstractReferenceEditService<IncomeItem> {

    public IncomeItemEditService(
            final BotService botService,
            final IncomeItemRepository incomeItemRepository
    ) {
        super(botService, incomeItemRepository, IncomeItem.class);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

}
