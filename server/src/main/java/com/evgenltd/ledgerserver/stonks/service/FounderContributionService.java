package com.evgenltd.ledgerserver.stonks.service;

import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.common.service.DocumentService;
import com.evgenltd.ledgerserver.stonks.entity.FounderContribution;
import com.evgenltd.ledgerserver.stonks.record.FounderContributionRecord;
import com.evgenltd.ledgerserver.stonks.record.FounderContributionRow;
import com.evgenltd.ledgerserver.stonks.repository.FounderContributionRepository;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;

@Service
public class FounderContributionService extends DocumentService<FounderContribution, FounderContributionRecord, FounderContributionRow> {

    public FounderContributionService(
            final FounderContributionRepository founderContributionRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
        super(founderContributionRepository, journalEntryRepository);
    }

    @Override
    protected FounderContributionRow toRow(final FounderContribution founderContribution) {
        return new FounderContributionRow(
            founderContribution.getId(),
            founderContribution.getName(),
            founderContribution.getAmount(),
            founderContribution.getAccount().getId(),
            founderContribution.getAccount().getName()
        );
    }

    @Override
    protected  FounderContributionRecord toRecord(final FounderContribution founderContribution) {
        return new FounderContributionRecord(
            founderContribution.getId(),
            founderContribution.getName(),
            founderContribution.getAmount(),
            new ReferenceRecord(
                founderContribution.getAccount().getId(),
                founderContribution.getAccount().getName()
            )
        );
    }

    @Override
    protected  FounderContribution toEntity(final FounderContributionRecord founderContributionRecord) {
        final FounderContribution founderContribution = new FounderContribution();
        founderContribution.setId(founderContributionRecord.id());
        founderContribution.setName(founderContributionRecord.name());
        founderContribution.setAmount(founderContributionRecord.amount());
        final Account account = new Account();
        account.setId(founderContributionRecord.account().id());
        account.setName(founderContributionRecord.account().name());
        founderContribution.setAccount(account);
        return founderContribution;
    }

}