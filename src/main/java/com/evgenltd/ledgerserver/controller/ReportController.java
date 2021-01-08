package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.record.CodeCard;
import com.evgenltd.ledgerserver.record.Turnover;
import com.evgenltd.ledgerserver.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(final ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/turnover")
    public String turnover(
            @RequestParam(value = "from", required = false) final String from,
            @RequestParam(value = "to", required = false) final String to,
            final Model model
    ) {
        final LocalDate fromDate = from != null
                ? LocalDate.parse(from)
                : LocalDate.of(2020, 1, 1);
        final LocalDate toDate = to != null
                ? LocalDate.parse(to)
                : LocalDate.of(2030, 1, 1);

        final List<Turnover> turnover = reportService.turnover(fromDate.atStartOfDay(), toDate.atStartOfDay());

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("turnover", turnover);
        model.addAttribute("total", reportService.totalTurnover(turnover));

        return "turnover";
    }

    @GetMapping("/code_card/{code}")
    public String codeCard(
            @PathVariable("code") final String code,
            @RequestParam(value = "from", required = false) final String from,
            @RequestParam(value = "to", required = false) final String to,
            final Model model
    ) {
        final LocalDate fromDate = from != null && !from.equals("null")
                ? LocalDate.parse(from)
                : LocalDate.of(2020, 1, 1);
        final LocalDate toDate = to != null && !to.equals("null")
                ? LocalDate.parse(to)
                : LocalDate.of(2030, 1, 1);

        final CodeCard codeCard = reportService.codeCard(
                code,
                fromDate.atStartOfDay(),
                toDate.atStartOfDay()
        );

        model.addAttribute("data", codeCard);
        model.addAttribute("code", code);

        return "codeCard";
    }

}
