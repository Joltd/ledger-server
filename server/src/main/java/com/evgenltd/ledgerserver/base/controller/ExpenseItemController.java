package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.entity.ExpenseItem;
import com.evgenltd.ledgerserver.base.record.ExpenseItemRecord;
import com.evgenltd.ledgerserver.base.record.ExpenseItemRow;
import com.evgenltd.ledgerserver.base.service.ExpenseItemService;
import com.evgenltd.ledgerserver.common.controller.ReferenceController;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity/common/expense-item")
public class ExpenseItemController extends ReferenceController<ExpenseItem, ExpenseItemRecord, ExpenseItemRow> {
    public ExpenseItemController(final ExpenseItemService expenseItemService) {
        super(expenseItemService);
    }
}