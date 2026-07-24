package com.example.SpringAiRagApp.dto;

public record Citation(
        String source,
        Object pageNumber,
        Object chunkIndex
) {
}
