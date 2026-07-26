package com.example.SpringAiRagApp.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<Citation> sources,
        String sessionId
) {
}
