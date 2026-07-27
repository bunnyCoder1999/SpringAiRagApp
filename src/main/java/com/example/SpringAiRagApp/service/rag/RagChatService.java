package com.example.SpringAiRagApp.service.rag;

import com.example.SpringAiRagApp.dto.ChatResponse;
import com.example.SpringAiRagApp.dto.Citation;
import com.example.SpringAiRagApp.service.search.SemanticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RagChatService {
    private final SemanticSearchService searchService;
    private final ChatClient.Builder chatClientBuilder;
    private final ChatMemory chatMemory;
    private final ResourceLoader resourceLoader;
    private final Logger log = LoggerFactory.getLogger(RagChatService.class);

    public RagChatService(SemanticSearchService searchService,
                          ChatClient.Builder chatClientBuilder,
                          ChatMemory chatMemory,
                          ResourceLoader resourceLoader) {
        this.searchService = searchService;
        this.chatClientBuilder = chatClientBuilder;
        this.chatMemory = chatMemory;
        this.resourceLoader = resourceLoader;
    }

    public ChatResponse ask(String question, String sessionId) {
        log.info("Received question: '{}'", question);

        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        List<Document> chunks = searchService.search(question);
        log.info("Found {} relevant chunks", chunks.size());

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

        ChatClient sessionClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10))
                .build();

        String answer = sessionClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        log.info("Answer generated for question '{}' with {} sources", question, citations);
        return new ChatResponse(answer, citations, sessionId);
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

    public Flux<String> askStream (String question, String sessionId){
        log.info("Received question: '{}'", question);

        if(sessionId == null || sessionId.isBlank()){
            sessionId = UUID.randomUUID().toString();
        }

        List<Document> chunks = searchService.search(question);
        log.info("Found {} relevant chunks", chunks.size());

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
                .replace("{context}" , context);

        ChatClient sessionClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10))
                .build();

        Flux<String> stream = sessionClient.prompt()
                .system(systemPrompt)
                .user(question)
                .stream()
                .content();
        log.info("Answer generated for question '{}' with {} sources", question, citations);
        return stream;
    }
}
