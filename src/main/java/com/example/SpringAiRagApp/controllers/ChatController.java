package com.example.SpringAiRagApp.controllers;

import com.example.SpringAiRagApp.dto.ChatResponse;
import com.example.SpringAiRagApp.service.rag.RagChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        if(question == null || question.isBlank()){
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatService.ask(question);
        return ResponseEntity.ok(response);
    }
}
