package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.record.CodeCard;
import com.evgenltd.ledgerserver.record.Turnover;
import com.evgenltd.ledgerserver.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(final ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/turnover")
    public String turnover(final Model model) {
        final List<Turnover> turnover = reportService.turnover(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2021, 1, 1, 0, 0)
        );

        model.addAttribute("turnover", turnover);

        model.addAttribute("total", reportService.totalTurnover(turnover));

        return "turnover";
    }

    @GetMapping("/code_card/{code}")
    public String codeCard(@PathVariable("code") final String code, final Model model) {
        final CodeCard codeCard = reportService.codeCard(
                code,
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2021, 1, 1, 0, 0)
        );

        model.addAttribute("data", codeCard);
        model.addAttribute("code", code);

        return "codeCard";
    }

}
