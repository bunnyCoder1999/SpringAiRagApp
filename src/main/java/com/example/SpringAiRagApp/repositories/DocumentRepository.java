package com.example.SpringAiRagApp.repositories;

import com.example.SpringAiRagApp.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}
