package com.evgenltd.ledgerserver.document.common.controller;

import com.evgenltd.ledgerserver.document.common.record.DocumentRecord;
import com.evgenltd.ledgerserver.document.common.repository.DocumentRepository;
import com.evgenltd.ledgerserver.document.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.document.common.service.DocumentService;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.common.filter.LoadConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/document/browser")
public class DocumentBrowserController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;

    public DocumentBrowserController(
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
                DtoField.builder().reference("id").type(FieldType.NUMBER).build(),
                DtoField.builder().reference("date").sort(true).type(FieldType.DATE).format("yyyy-MM-dd").build(),
                DtoField.builder().reference("type").sort(true).type(FieldType.ENUM).localizationKey("document.type.").build(),
                DtoField.builder().reference("comment").type(FieldType.STRING).build()
        ));
    }

    @GetMapping("/descriptor/meta")
    public MetaModel meta() {
        return new MetaModel(Arrays.asList(
                MetaField.builder().reference("id").type(FieldType.NUMBER).build(),
                MetaField.builder().reference("date").type(FieldType.DATE).build(),
                MetaField.builder().reference("type").type(FieldType.STRING).build(),
                MetaField.builder().reference("comment").type(FieldType.STRING).build()
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        journalEntryRepository.deleteByDocumentId(id);
        documentRepository.deleteById(id);
    }


}
