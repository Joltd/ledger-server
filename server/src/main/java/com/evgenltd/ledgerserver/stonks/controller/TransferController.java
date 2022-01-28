package com.evgenltd.ledgerserver.stonks.controller;

import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.common.service.ReferenceService;
import com.evgenltd.ledgerserver.stonks.entity.Transfer;
import com.evgenltd.ledgerserver.stonks.record.TransferRecord;
import com.evgenltd.ledgerserver.stonks.record.TransferRow;
import com.evgenltd.ledgerserver.stonks.service.TransferService;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/stonks/transfer")
public class TransferController extends ReferenceController<Transfer, TransferRecord, TransferRow> {
    public TransferController(final TransferService transferService) {
        super(transferService);
    }
}