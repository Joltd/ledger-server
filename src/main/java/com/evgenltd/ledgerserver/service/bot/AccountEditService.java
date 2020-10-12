package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.repository.AccountRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AccountEditService extends AbstractReferenceEditService<Account> {

    public AccountEditService(
            final BotService botService,
            final AccountRepository accountRepository
    ) {
        super(botService, accountRepository, Account.class);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

}
