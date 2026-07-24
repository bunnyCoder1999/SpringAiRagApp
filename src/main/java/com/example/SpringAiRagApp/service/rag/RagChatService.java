package com.example.SpringAiRagApp.service.rag;

import com.example.SpringAiRagApp.dto.ChatResponse;
import com.example.SpringAiRagApp.dto.Citation;
import com.example.SpringAiRagApp.service.search.SemanticSearchService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagChatService {
    private final SemanticSearchService searchService;
    private final ChatClient chatClient;
    private final ResourceLoader resourceLoader;

    public RagChatService(SemanticSearchService searchService,
                          ChatClient.Builder chatClientBuilder,
                          ResourceLoader resourceLoader){
        this.searchService = searchService;
        this.chatClient = chatClientBuilder.build();
        this.resourceLoader = resourceLoader;
    }

    public ChatResponse ask(String question){
        List<Document> chunks = searchService.search(question);

        String context = chunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        List<Citation> citations = chunks.stream()
                .map(chunk -> new Citation(
                    String.valueOf(chunk.getMetadata().get("source")),
                    chunk.getMetadata().get("page_number"),
                    chunk.getMetadata().get("chunk_index")
            ))
            .distinct()
            .toList();

        String systemPrompt = loadPromptTemplate()
                .replace("{context}", context);

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        return new ChatResponse(answer, citations);
    }

    private String loadPromptTemplate() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        resourceLoader.getResource("classpath:prompts/rag-system-prompt.st")
                                .getInputStream(),
                        StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt template", e);
        }
    }
}
