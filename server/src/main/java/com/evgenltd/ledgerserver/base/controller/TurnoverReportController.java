package com.evgenltd.ledgerserver.base.controller;

import com.evgenltd.ledgerserver.base.record.TurnoverRecord;
import com.evgenltd.ledgerserver.base.service.TurnoverReportService;
import com.evgenltd.ledgerserver.util.filter.FilterConfig;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/report/turnover")
public class TurnoverReportController {

    private final TurnoverReportService turnoverReportService;

    public TurnoverReportController(final TurnoverReportService turnoverReportService) {
        this.turnoverReportService = turnoverReportService;
    }

    @PostMapping
    public TurnoverRecord turnover(@RequestBody final TurnoverReportRequest request) {
        return turnoverReportService.turnover(request.from, request.to, request.filter);
    }

    public record TurnoverReportRequest(LocalDateTime from, LocalDateTime to, FilterConfig filter) {}

}
