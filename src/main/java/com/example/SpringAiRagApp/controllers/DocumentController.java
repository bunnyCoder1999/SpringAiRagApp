package com.example.SpringAiRagApp.controllers;

import com.example.SpringAiRagApp.dto.DocumentUploadResponse;
import com.example.SpringAiRagApp.service.ingestion.DocumentIngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        DocumentUploadResponse response = documentIngestionService.ingest(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
