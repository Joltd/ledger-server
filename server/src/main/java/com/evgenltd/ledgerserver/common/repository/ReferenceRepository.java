package com.evgenltd.ledgerserver.common.repository;

import com.evgenltd.ledgerserver.common.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReferenceRepository<T extends Reference> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {}
