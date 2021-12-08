package com.evgenltd.ledgerserver.document.controller;

import com.evgenltd.ledgerserver.document.entity.Document;
import com.evgenltd.ledgerserver.document.repository.DocumentRepository;
import com.evgenltd.ledgerserver.document.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.*;
import com.evgenltd.ledgerserver.platform.browser.record.loadconfig.LoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;

    public DocumentController(
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
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
    public List<Document> load(@RequestBody final LoadConfig loadConfig) {
        return documentRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        journalEntryRepository.deleteByDocumentId(id);
        documentRepository.deleteById(id);
    }


}
