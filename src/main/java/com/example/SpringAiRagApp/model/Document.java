package com.example.SpringAiRagApp.model;

import com.example.SpringAiRagApp.enums.DocumentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "documents")
@Entity
public class Document {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    private Integer totalChunks;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentChunk> chunks;

 // Getters

    public UUID getId() {
        return id;
    }

    public String getFilename(){
        return filename;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public LocalDateTime getUploadedAt(){
        return uploadedAt;
    }

// Setters

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

}
