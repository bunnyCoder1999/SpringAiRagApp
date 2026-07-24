package com.example.SpringAiRagApp.service.ingestion;

import com.example.SpringAiRagApp.dto.DocumentUploadResponse;
import com.example.SpringAiRagApp.enums.DocumentStatus;
import com.example.SpringAiRagApp.model.Document;
import com.example.SpringAiRagApp.model.DocumentChunk;
import com.example.SpringAiRagApp.repositories.DocumentChunkRepository;
import com.example.SpringAiRagApp.repositories.DocumentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class DocumentIngestionService {
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentParser documentParser;
    private final DocumentChunker documentChunker;
    private final MetaDataEnricher metaDataEnricher;
    private final VectorStore vectorStore;
    private final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    public DocumentIngestionService(DocumentRepository documentRepository,
                                    DocumentChunkRepository documentChunkRepository,
                                    DocumentParser documentParser,
                                    DocumentChunker documentChunker,
                                    MetaDataEnricher metaDataEnricher,
                                    VectorStore vectorStore) {
        this.documentChunkRepository = documentChunkRepository;
        this.documentRepository = documentRepository;
        this.documentParser = documentParser;
        this.documentChunker = documentChunker;
        this.metaDataEnricher = metaDataEnricher;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public DocumentUploadResponse ingest (MultipartFile file){
        String fileName = file.getOriginalFilename();

        Document doc = new Document();
        doc.setFilename(fileName);
        doc.setStatus(DocumentStatus.PENDING);
        doc.setUploadedAt(LocalDateTime.now());
        doc = documentRepository.save(doc);

        try {
            List<org.springframework.ai.document.Document> parsedDocs = documentParser.parse(file.getResource());
            List<org.springframework.ai.document.Document> chunks = documentChunker.chunk(parsedDocs);
            chunks = metaDataEnricher.enrich(chunks, fileName, doc.getId());

            vectorStore.add(chunks);
            log.info("Document '{}' ingested successfully with {} chunks", doc.getFilename(), doc.getTotalChunks());

            List<DocumentChunk> chunkEntities = new ArrayList<>();
            for(int i = 0; i < chunks.size(); i++){
                org.springframework.ai.document.Document aiChunk = chunks.get(i);
                DocumentChunk entity = new DocumentChunk();
                entity.setDocument(doc);
                entity.setChunkIndex(i);
                entity.setContent(aiChunk.getText());
                entity.setMetadata(aiChunk.getMetadata().toString());
                chunkEntities.add(entity);
            }
            documentChunkRepository.saveAll(chunkEntities);

            doc.setStatus(DocumentStatus.INDEXED);
            doc.setTotalChunks(chunks.size());
            documentRepository.save(doc);

            return new DocumentUploadResponse(
                    doc.getId(),
                    doc.getFilename(),
                    doc.getStatus(),
                    doc.getTotalChunks(),
                    doc.getUploadedAt()
            );
        }
        catch (Exception e){
            doc.setStatus(DocumentStatus.FAILED);
            documentRepository.save(doc);
            throw new RuntimeException("Ingestion failed for file: " + doc.getFilename(), e);
        }

    }
}
