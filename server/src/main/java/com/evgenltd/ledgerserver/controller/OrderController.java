package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/descriptor")
    public Descriptor descriptor() {
        return new Descriptor(
                new DtoModel(Arrays.asList(
                        new DtoField("id", false, null),
                        new DtoField("name", true, null),
                        new DtoField("enabled", true, null),
                        new DtoField("length", true, null),
                        new DtoField("amount", true, null),
                        new DtoField("time", true, null),
                        new DtoField("date", true, null),
                        new DtoField("personId", true, null),
                        new DtoField("personName", true, null)
                )),
                new MetaModel(Arrays.asList(
                        new MetaField("id", MetaType.NUMBER, null),
                        new MetaField("name", MetaType.STRING, null),
                        new MetaField("enabled", MetaType.BOOlEAN, null),
                        new MetaField("length", MetaType.NUMBER, null),
                        new MetaField("amount", MetaType.NUMBER, null),
                        new MetaField("time", MetaType.DATE, null),
                        new MetaField("date", MetaType.DATE, null),
                        new MetaField("person", MetaType.OBJECT, Arrays.asList(
                                new MetaField("id", MetaType.NUMBER, null),
                                new MetaField("name", MetaType.STRING, null)
                        ))
                ))
        );
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return orderRepository.count(/* filter config to spec */);
    }

    @PostMapping("/")
    public List<OrderRecord> load(@RequestBody final LoadConfig loadConfig) {
        return orderRepository.findAll(
                // filter config to spec
                loadConfig.pageAndSortConfig()
        ).stream()
                .map(order -> new OrderRecord(
                        order.getId(),
                        order.getName(),
                        order.getEnabled(),
                        order.getLength(),
                        order.getAmount(),
                        order.getTime(),
                        order.getDate(),
                        order.getPerson().getId(),
                        order.getPerson().getName()
                ))
                .collect(Collectors.toList());
    }

    public static record OrderRecord(
            Long id,
            String name,
            Boolean enabled,
            Integer length,
            BigDecimal amount,
            LocalDateTime time,
            LocalDate date,
            Long personId,
            String personName
    ) {}

}
