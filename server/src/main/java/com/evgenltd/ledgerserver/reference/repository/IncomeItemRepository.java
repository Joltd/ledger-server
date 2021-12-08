package com.evgenltd.ledgerserver.reference.repository;

import com.evgenltd.ledgerserver.reference.entity.IncomeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeItemRepository extends JpaRepository<IncomeItem, Long>, JpaSpecificationExecutor<IncomeItem> {}
