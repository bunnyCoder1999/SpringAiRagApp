package com.example.SpringAiRagApp.service.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MetaDataEnricher {

    public List<Document> enrich(List<Document> chunks, String filename, UUID docId) {
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            Map<String, Object> metadata = chunk.getMetadata();
            metadata.put("source", filename);
            metadata.put("page_number", metadata.getOrDefault("page_number", null));
            metadata.put("doc_id", docId.toString());
            metadata.put("chunk_index", i);
        }
        return chunks;
    }
}