package com.evgenltd.ledgerserver.document.common.controller;

import com.evgenltd.ledgerserver.document.common.entity.Document;
import com.evgenltd.ledgerserver.document.common.service.DocumentService;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document/editor")
public class DocumentEditorController {

    private final DocumentService documentService;


    public DocumentEditorController(final DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{type}/descriptor/meta")
    public MetaModel meta(@PathVariable("type") final String type) {
        return documentService.resolveDefinition(Document.Type.byId(type)).documentMeta();
    }

    @GetMapping("/{id}")
    public JsonNode byId(@PathVariable("id") final Long id) {
        return documentService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final ObjectNode document) {
        documentService.update(document);
    }


}
