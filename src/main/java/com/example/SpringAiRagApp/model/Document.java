package com.example.SpringAiRagApp.model;

import com.example.SpringAiRagApp.Enums.DocumentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
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

 // Getters

    public UUID getId() {
        return id;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

// Setters

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }
}
