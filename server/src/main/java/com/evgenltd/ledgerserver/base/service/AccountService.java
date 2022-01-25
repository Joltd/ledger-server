package com.evgenltd.ledgerserver.base.service;


import com.evgenltd.ledgerserver.base.entity.Account;
import com.evgenltd.ledgerserver.base.record.AccountRecord;
import com.evgenltd.ledgerserver.base.record.AccountRow;
import com.evgenltd.ledgerserver.base.repository.AccountRepository;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends ReferenceService<Account, AccountRecord, AccountRow> {

    public AccountService(
            final SettingService settingService,
            final AccountRepository accountRepository
    ) {
        super(settingService, accountRepository);
    }

    @Override
    protected AccountRow toRow(final Account account) {
        return new AccountRow(
            account.getId(),
            account.getName()
        );
    }

    @Override
    protected AccountRecord toRecord(final Account account) {
        return new AccountRecord(
            account.getId(),
            account.getName()
        );
    }

    @Override
    protected Account toEntity(final AccountRecord accountRecord) {
        final Account account = new Account();
        account.setId(accountRecord.id());
        account.setName(accountRecord.name());
        return account;
    }

}