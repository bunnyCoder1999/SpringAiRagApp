package com.example.SpringAiRagApp.service.ingestion;


import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentChunker {
    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int MIN_CHUNK_SIZE_CHARS = 350;
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 5;
    private static final int MAX_NUM_CHUNKS = 10000;

    public List<Document> chunk(List<Document> documents){
        TokenTextSplitter textSplitter = new TokenTextSplitter(
                DEFAULT_CHUNK_SIZE,
                MIN_CHUNK_SIZE_CHARS,
                MIN_CHUNK_LENGTH_TO_EMBED,
                MAX_NUM_CHUNKS,
                true
        );
        return textSplitter.apply(documents);
    }

}
