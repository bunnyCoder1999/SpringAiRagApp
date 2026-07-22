package com.example.SpringAiRagApp.repositories;

import com.example.SpringAiRagApp.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(UUID documentId);
}
