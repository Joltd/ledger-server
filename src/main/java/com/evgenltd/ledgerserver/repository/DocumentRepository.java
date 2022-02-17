package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Document findByDate(LocalDateTime date);

    Document findByExternalId(String externalId);

}
