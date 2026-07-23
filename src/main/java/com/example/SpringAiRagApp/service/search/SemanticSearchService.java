package com.example.SpringAiRagApp.service.search;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanticSearchService {
    private final VectorStore vectorStore;

    public SemanticSearchService(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    public List<Document> search(String query, int topK, double similarityThreshold, String filterExpression) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(similarityThreshold);

        if (filterExpression != null && !filterExpression.isBlank()) {
            Filter.Expression expression =  new FilterExpressionTextParser().parse(filterExpression);
            builder.filterExpression(expression);
        }

        return vectorStore.similaritySearch(builder.build());
    }

    public List<Document> search(String query, int topK, double similarityThreshold) {
        return search(query, topK, similarityThreshold, null);
    }

    public List<Document> search(String query, int topK) {
        return search(query, topK, 0.7, null);
    }

    public List<Document> search(String query) {
        return search(query, 5, 0.7, null);
    }
}
