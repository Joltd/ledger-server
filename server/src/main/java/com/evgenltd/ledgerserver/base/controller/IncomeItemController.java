package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.entity.IncomeItem;
import com.evgenltd.ledgerserver.base.record.IncomeItemRecord;
import com.evgenltd.ledgerserver.base.record.IncomeItemRow;
import com.evgenltd.ledgerserver.base.service.IncomeItemService;
import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/income-item")
public class IncomeItemController extends ReferenceController<IncomeItem, IncomeItemRecord, IncomeItemRow> {
    public IncomeItemController(final IncomeItemService incomeItemService) {
        super(incomeItemService);
    }
}