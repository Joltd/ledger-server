package com.evgenltd.ledgerserver.common.service;


import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.common.record.AccountRecord;
import com.evgenltd.ledgerserver.common.record.AccountRow;
import com.evgenltd.ledgerserver.common.repository.AccountRepository;
import com.evgenltd.ledgerserver.util.ApplicationException;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService extends ReferenceService<Account, AccountRecord, AccountRow> {

    public AccountService(final AccountRepository accountRepository) {
        super(accountRepository);
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