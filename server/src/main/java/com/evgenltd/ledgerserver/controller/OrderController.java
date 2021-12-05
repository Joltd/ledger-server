package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.browser.record.loadconfig.Filter;
import com.evgenltd.ledgerserver.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
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
                        new DtoField("id", false, FieldType.NUMBER, null),
                        new DtoField("name", true, FieldType.STRING, null),
                        new DtoField("enabled", true, FieldType.BOOlEAN, null),
                        new DtoField("length", true, FieldType.NUMBER, "5.1-2"),
                        new DtoField("amount", true, FieldType.NUMBER, "8.4-4"),
                        new DtoField("time", true, FieldType.DATE, "yy MMMM dd h:mm"),
                        new DtoField("date", true, FieldType.DATE, "MMMM dd"),
                        new DtoField("personId", true, FieldType.NUMBER, null),
                        new DtoField("personName", true, FieldType.STRING, null)
                )),
                new MetaModel(Arrays.asList(
                        new MetaField("id", FieldType.NUMBER, null),
                        new MetaField("name", FieldType.STRING, null),
                        new MetaField("enabled", FieldType.BOOlEAN, null),
                        new MetaField("length", FieldType.NUMBER, null),
                        new MetaField("amount", FieldType.NUMBER, null),
                        new MetaField("time", FieldType.DATE, null),
                        new MetaField("date", FieldType.DATE, null),
                        new MetaField("person", FieldType.OBJECT, Arrays.asList(
                                new MetaField("id", FieldType.NUMBER, null),
                                new MetaField("name", FieldType.STRING, null)
                        ))
                ))
        );
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return orderRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<OrderRecord> load(@RequestBody final LoadConfig loadConfig) {
        return orderRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
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
