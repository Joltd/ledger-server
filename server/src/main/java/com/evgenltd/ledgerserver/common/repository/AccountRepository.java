package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends ReferenceRepository<Account> {}