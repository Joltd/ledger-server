package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.entity.Account;
import com.evgenltd.ledgerserver.base.record.AccountRecord;
import com.evgenltd.ledgerserver.base.record.AccountRow;
import com.evgenltd.ledgerserver.base.service.AccountService;
import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/account")
public class AccountController extends ReferenceController<Account, AccountRecord, AccountRow> {
    public AccountController(final AccountService accountService) {
        super(accountService);
    }
}