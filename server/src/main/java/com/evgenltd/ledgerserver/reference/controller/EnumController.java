package com.evgenltd.ledgerserver.reference.controller;

import com.evgenltd.ledgerserver.reference.entity.Currency;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/reference")
public class EnumController {

    @GetMapping("/currency")
    public List<Currency> currencies(@QueryParam("filter") final String filter) {
        return Stream.of(Currency.values())
                .filter(currency -> StringUtils.isBlank(filter) || currency.name().toLowerCase().contains(filter))
                .collect(Collectors.toList());
    }

}
