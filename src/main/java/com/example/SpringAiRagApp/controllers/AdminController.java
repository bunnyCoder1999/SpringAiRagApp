package com.example.SpringAiRagApp.controllers;

import com.example.SpringAiRagApp.model.Document;
import com.example.SpringAiRagApp.service.ingestion.DocumentIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final DocumentIngestionService documentIngestionService;


    public AdminController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> findAllDocuments(){
        List<Document> documents = documentIngestionService.fetchAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Optional<Document>> findDocumentById(@RequestParam("id") UUID id){
        if(id == null){
            return ResponseEntity.badRequest().build();
        }
        Optional<Document> document = documentIngestionService.fetchDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/documents/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") UUID id) {
        documentIngestionService.deleteDocument(id);
        return ResponseEntity.ok("Successfully deleted document id: " + id);
    }
}
