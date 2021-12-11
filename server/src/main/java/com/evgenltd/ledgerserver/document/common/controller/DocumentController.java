package com.evgenltd.ledgerserver.document.common.controller;

import com.evgenltd.ledgerserver.document.common.record.DocumentRecord;
import com.evgenltd.ledgerserver.document.common.repository.DocumentRepository;
import com.evgenltd.ledgerserver.document.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.document.common.service.DocumentService;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;

    public DocumentController(
            final DocumentService documentService,
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    @GetMapping("/descriptor/dto")
    public DtoModel dto() {
        return new DtoModel(Arrays.asList(
                new DtoField("id", false, FieldType.NUMBER, null),
                new DtoField("date", true, FieldType.DATE, null),
                new DtoField("type", true, FieldType.STRING, null),
                new DtoField("comment", false, FieldType.STRING, null)
        ));
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                new MetaField("id", FieldType.NUMBER, null),
                new MetaField("date", FieldType.DATE, null),
                new MetaField("type", FieldType.STRING, null),
                new MetaField("comment", FieldType.STRING, null)
        ));
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return documentRepository.count(loadConfig.toSpecification());
    }

    @PostMapping("/")
    public List<DocumentRecord> load(@RequestBody final LoadConfig loadConfig) {
        return documentRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(document -> new DocumentRecord(document.getId(), document.getDate(), document.getType(), document.getComment()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public JsonNode byId(@PathVariable("id") final Long id) {
        return documentService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final ObjectNode document) {
        documentService.update(document);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        journalEntryRepository.deleteByDocumentId(id);
        documentRepository.deleteById(id);
    }


}
