package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.entity.Person;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.evgenltd.ledgerserver.entity.Order;
import com.evgenltd.ledgerserver.repository.OrderRepository;
import com.evgenltd.ledgerserver.repository.PersonRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
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
    private final PersonRepository personRepository;

    public OrderController(
            final OrderRepository orderRepository,
            final PersonRepository personRepository
    ) {
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
    }

    @GetMapping("/descriptor")
    public Descriptor descriptor() {
        return new Descriptor(
                new DtoModel(Arrays.asList(
                        new DtoField("id", false, FieldType.NUMBER, null),
                        new DtoField("name", true, FieldType.STRING, null),
                        new DtoField("enabled", true, FieldType.BOOLEAN, null),
                        new DtoField("length", true, FieldType.NUMBER, "5.1-2"),
                        new DtoField("amount", true, FieldType.NUMBER, "8.4-4"),
                        new DtoField("time", true, FieldType.DATE, "yy MMMM dd h:mm"),
                        new DtoField("date", true, FieldType.DATE, "MMMM dd"),
                        new DtoField("personId", true, FieldType.NUMBER, null),
                        new DtoField("personName", true, FieldType.STRING, null)
                )),
                meta()
        );
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                new MetaField("id", FieldType.NUMBER, null),
                new MetaField("name", FieldType.STRING, null),
                new MetaField("enabled", FieldType.BOOLEAN, null),
                new MetaField("length", FieldType.NUMBER, null),
                new MetaField("amount", FieldType.NUMBER, null),
                new MetaField("time", FieldType.DATE, null),
                new MetaField("date", FieldType.DATE, null),
                new MetaField("person", FieldType.OBJECT, Arrays.asList(
                        new MetaField("id", FieldType.NUMBER, null),
                        new MetaField("name", FieldType.STRING, null)
                ))
        ));
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

    @GetMapping("/{id}")
    public Order byId(@PathVariable("id") final Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ApplicationException("Entity [%s] not found", id));
//        return new Entity(Arrays.asList(
//                new EntityProperty("id", String.valueOf(order.getId()), null),
//                new EntityProperty("name", order.getName(), null),
//                new EntityProperty("enabled", String.valueOf(order.getEnabled()), null),
//                new EntityProperty("length", String.valueOf(order.getLength()), null),
//                new EntityProperty("length", String.valueOf(order.getAmount()), null),
//                new EntityProperty("person", String.valueOf(order.getPerson().getId()), order.getPerson().getName())
//        ));
    }

    @PostMapping
    public void update(@RequestBody final Order order) {
        orderRepository.save(order);
    }

    @GetMapping("/person")
    public List<Person> persons(@QueryParam("filter") final String filter) {
        return personRepository.findAll().stream()
                .filter(person -> StringUtils.isBlank(filter) || person.getName().contains(filter))
                .toList();
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
