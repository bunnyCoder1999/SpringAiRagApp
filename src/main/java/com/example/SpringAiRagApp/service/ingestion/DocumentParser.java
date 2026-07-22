package com.example.SpringAiRagApp.service.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentParser {

    public List<Document> parse(Resource resource) {
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        return reader.get();
    }
}
