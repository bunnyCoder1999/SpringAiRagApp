package com.example.SpringAiRagApp.controllers;

import com.example.SpringAiRagApp.dto.ChatResponse;
import com.example.SpringAiRagApp.service.rag.RagChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final RagChatService chatService;

    public ChatController(RagChatService chatService){
        this.chatService = chatService;
    }
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion (@RequestBody Map<String, String> request){
        String question = request.get("question");
        String sessionId = request.get("sessionId");
        if(question == null || question.isBlank()){
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatService.ask(question, sessionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask/stream")
    public SseEmitter askStream (@RequestBody Map<String, String> request){
        SseEmitter sseEmitter = new SseEmitter(300000L);

        String question = request.get("question");
        String sessionId = request.get("sessionId");

        if (question == null || question.isBlank()) {
            sseEmitter.completeWithError(new IllegalArgumentException("Question must not be blank"));
            return sseEmitter;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Flux<String> stringFlux = chatService.askStream(question, sessionId);
            stringFlux.subscribe(
                    token -> { try { sseEmitter.send(SseEmitter.event().data(token)); } catch (IOException e) { sseEmitter.completeWithError(e); }},
                    sseEmitter::completeWithError,
                    sseEmitter::complete
            );
        });
        executor.shutdown();
    return sseEmitter;
    }
}
