package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.record.CurrencyBalance;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.repository.DocumentRepository;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final DocumentRepository documentRepository;

    public JournalService(
            final JournalEntryRepository journalEntryRepository,
            final DocumentRepository documentRepository
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.documentRepository = documentRepository;
    }


}
