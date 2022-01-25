package com.evgenltd.ledgerserver.stonks.service;

import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import com.evgenltd.ledgerserver.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.common.service.DocumentService;
import com.evgenltd.ledgerserver.stonks.entity.Transfer;
import com.evgenltd.ledgerserver.stonks.record.TransferRecord;
import com.evgenltd.ledgerserver.stonks.record.TransferRow;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;

@Service
public class TransferService extends DocumentService<Transfer, TransferRecord, TransferRow> {

    public TransferService(
            final ReferenceRepository<Transfer> referenceRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
        super(referenceRepository, journalEntryRepository);
    }

    @Override
    protected TransferRow toRow(final Transfer transfer) {
        return new TransferRow(
                transfer.getId(),
                transfer.getName(),
                transfer.getAmount(),
                transfer.getFrom().getId(),
                transfer.getFrom().getName(),
                transfer.getTo().getId(),
                transfer.getTo().getName()
        );
    }

    @Override
    protected TransferRecord toRecord(final Transfer transfer) {
        return new TransferRecord(
                transfer.getId(),
                transfer.getName(),
                transfer.getAmount(),
                new ReferenceRecord(
                        transfer.getFrom().getId(),
                        transfer.getFrom().getName()
                ),
                new ReferenceRecord(
                        transfer.getTo().getId(),
                        transfer.getTo().getName()
                )
        );
    }

    @Override
    protected Transfer toEntity(final TransferRecord transferRecord) {
        final Transfer transfer = new Transfer();
        transfer.setId(transferRecord.id());
        transfer.setName(transferRecord.name());
        transfer.setAmount(transferRecord.amount());
        final Account from = new Account();
        from.setId(transferRecord.from().id());
        from.setName(transferRecord.from().name());
        transfer.setFrom(from);
        final Account to = new Account();
        to.setId(transferRecord.to().id());
        to.setName(transferRecord.to().name());
        transfer.setTo(to);
        return transfer;
    }

}