package com.evgenltd.ledgerserver.stonks.repository;

import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import com.evgenltd.ledgerserver.stonks.entity.Transfer;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends ReferenceRepository<Transfer> {}