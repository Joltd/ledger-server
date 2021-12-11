package com.evgenltd.ledgerserver.document.common.service;

import com.evgenltd.ledgerserver.document.common.entity.Document;
import com.evgenltd.ledgerserver.document.common.entity.JournalEntry;
import com.evgenltd.ledgerserver.document.common.repository.DocumentRepository;
import com.evgenltd.ledgerserver.document.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final ObjectMapper objectMapper;

    public DocumentService(
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository,
            final ObjectMapper objectMapper
    ) {
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.objectMapper = objectMapper;
    }

    public JsonNode byId(final Long id) {
        final Document document = documentRepository.getById(id);
        final ObjectNode documentNode = objectMapper.createObjectNode();
        documentNode.put("id", document.getId());
        documentNode.put("date", document.getDate().toString());
        documentNode.put("type", document.getType().name());

        final ObjectNode definition = readNode(document.getContent());
        documentNode.setAll(definition);
        return documentNode;
    }

    public void update(final ObjectNode documentNode) {
        final JsonNode idNode = documentNode.remove("id");
        final JsonNode type = documentNode.remove("type");
        Document document;
        if (idNode != null) {
            document = documentRepository.getById(idNode.asLong());
        } else {
            document = new Document();
            document.setType(Document.Type.valueOf(type.asText()));
        }

        final JsonNode date = documentNode.remove("date");
        document.setDate(LocalDateTime.parse(date.asText()));
        document.setContent(writeNode(documentNode));

        documentRepository.save(document);

        JournalEntry.builder()
                .accountId()
                .person()
                .build();
    }

    private ObjectNode readNode(final String content) {
        try {
            return (ObjectNode) objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to read document content");
        }
    }

    private String writeNode(final ObjectNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to write document content");
        }
    }

}
