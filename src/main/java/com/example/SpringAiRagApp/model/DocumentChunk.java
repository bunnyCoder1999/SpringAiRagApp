package com.example.SpringAiRagApp.model;

import jakarta.persistence.*;


import java.util.UUID;

@Table(name = "document_chunks")
@Entity
public class DocumentChunk {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Lob
    private String content;

    private String metadata;

    public DocumentChunk() {
    }

// Getters

    public UUID getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public String getMetadata() {
        return metadata;
    }

// Setters

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
