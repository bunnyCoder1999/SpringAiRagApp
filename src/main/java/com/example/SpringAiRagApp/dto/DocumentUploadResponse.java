package com.example.SpringAiRagApp.dto;

import com.example.SpringAiRagApp.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentUploadResponse(
        UUID id,
        String filename,
        DocumentStatus status,
        int totalChunks,
        LocalDateTime uploadedAt
) {
}
