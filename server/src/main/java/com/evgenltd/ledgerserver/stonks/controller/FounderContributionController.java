package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.stonks.entity.FounderContribution;
import com.evgenltd.ledgerserver.stonks.record.FounderContributionRecord;
import com.evgenltd.ledgerserver.stonks.record.FounderContributionRow;
import com.evgenltd.ledgerserver.stonks.service.FounderContributionService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/founder-contribution")
public class FounderContributionController extends ReferenceController<FounderContribution, FounderContributionRecord, FounderContributionRow> {
    public FounderContributionController(final FounderContributionService founderContributionService) {
        super(founderContributionService);
    }
}