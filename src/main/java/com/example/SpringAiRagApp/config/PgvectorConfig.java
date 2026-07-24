package com.example.SpringAiRagApp.config;

import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@ConditionalOnClass(name = "com.pgvector.PGvector")
public class PgvectorConfig {
    private static final Logger log = LoggerFactory.getLogger(PgvectorConfig.class);

    private final DataSource dataSource;

    public PgvectorConfig(DataSource dataSource) {
        this.dataSource = dataSource;
        log.info("PgvectorConfig created with DataSource: {}", dataSource);
    }

    @PostConstruct
    public void registerVectorType() {
        log.info("Registering vector type with JDBC driver...");
        try (Connection conn = dataSource.getConnection()) {
            PGvector.addVectorType(conn);
            log.info("Vector type registered successfully");
        } catch (Exception e) {
            log.error("Failed to register vector type", e);
            throw new RuntimeException("Failed to register vector type with JDBC driver", e);
        }
    }
}
