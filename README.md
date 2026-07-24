# Document Intelligence & Semantic Search Engine (RAG)

An enterprise **"Chat with your Documents"** platform for HR policies, financial reports, or technical documentation using **Retrieval-Augmented Generation (RAG)**.

Users upload PDFs, the service parses them, splits into chunks, converts them into vector embeddings, and stores them in a vector database. Users can then ask natural language questions grounded purely in those documents.

## Tech Stack

- **Backend:** Spring Boot 3.4, Spring AI 1.0.0-M6
- **Vector Database:** pgvector (PostgreSQL extension)
- **Local LLM:** Ollama (llama3.2 + nomic-embed-text)
- **Document Parsing:** Apache Tika
- **Build:** Maven

## Architecture

```
User Upload (PDF)
    ↓
DocumentParser (Apache Tika)
    ↓
DocumentChunker (TokenTextSplitter)
    ↓
MetaDataEnricher (source, page, chunk_index)
    ↓
VectorStore (pgvector)  +  JPA (documents table)
    ↓
User Query → SemanticSearchService → Ollama Embedding → pgvector Similarity Search
    ↓
RagChatService (LLM + context) → Answer + Source Citations
```

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven (or use the included `mvnw` wrapper)

## Quick Start

### 1. Clone and Configure

```bash
git clone <repo-url>
cd SpringAiRagApp
```

Copy the environment template and edit as needed:

```bash
cp .env.example .env
```

### 2. Start Infrastructure

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** with pgvector extension on port `5433`
- **Ollama** with `nomic-embed-text` (embeddings) and `llama3.2` (chat) on port `11434`

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## API Endpoints

### Upload a Document

```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@policy.pdf"
```

**Response:**

```json
{
  "id": "d64d7f71-5f8a-4003-8689-df7502fd0abc",
  "filename": "policy.pdf",
  "status": "INDEXED",
  "totalChunks": 39,
  "uploadedAt": "2026-07-24T01:52:36.989"
}
```

**Document Statuses:** `PENDING` → `INDEXED` | `FAILED`

### Ask a Question (via RagChatService)

The `RagChatService` class provides RAG-based Q&A internally. Expose it via a REST controller by injecting `RagChatService` and creating a `POST /api/chat/ask` endpoint.

**Expected response format:**

```json
{
  "answer": "Remote employees are entitled to 20 days of annual leave...",
  "sources": [
    { "source": "hr-policy.pdf", "pageNumber": 5, "chunkIndex": 12 },
    { "source": "hr-policy.pdf", "pageNumber": 6, "chunkIndex": 14 }
  ]
}
```

## Project Structure

```
src/main/java/com/example/SpringAiRagApp/
├── config/
│   └── PgvectorConfig.java          # JDBC vector type registration
├── controllers/
│   └── DocumentController.java      # POST /api/documents/upload
├── dto/
│   ├── DocumentUploadResponse.java
│   ├── ChatResponse.java
│   └── Citation.java
├── enums/
│   └── DocumentStatus.java          # PENDING, INDEXED, FAILED
├── model/
│   ├── Document.java                # JPA entity
│   └── DocumentChunk.java           # JPA entity
├── repositories/
│   ├── DocumentRepository.java
│   └── DocumentChunkRepository.java
└── service/
    ├── ingestion/
    │   ├── DocumentIngestionService.java
    │   ├── DocumentParser.java       # Apache Tika parsing
    │   ├── DocumentChunker.java      # TokenTextSplitter
    │   └── MetaDataEnricher.java     # source/page metadata
    ├── rag/
    │   └── RagChatService.java       # LLM Q&A with context
    └── search/
        └── SemanticSearchService.java # pgvector similarity search
```

## Configuration

### application.yml

| Property | Description | Default |
|---|---|---|
| `spring.ai.ollama.base-url` | Ollama server URL | `http://localhost:11434` |
| `spring.ai.ollama.chat.model` | LLM for chat | `llama3.2` |
| `spring.ai.ollama.embedding.model` | Model for embeddings | `nomic-embed-text` |
| `spring.ai.vectorstore.pgvector.dimensions` | Embedding dimensions | `768` |
| `spring.ai.vectorstore.pgvector.index-type` | pgvector index | `HNSW` |
| `spring.ai.vectorstore.pgvector.distance-type` | Distance metric | `COSINE_DISTANCE` |

### .env

| Variable | Description | Default |
|---|---|---|
| `POSTGRES_DB` | Database name | `ragdb` |
| `POSTGRES_USER` | DB user | `postgres` |
| `POSTGRES_PASSWORD` | DB password | `123456` |

## Data Flow

1. **Upload** — PDF is parsed by Apache Tika into raw text
2. **Chunk** — Text is split into 500-token overlapping chunks via `TokenTextSplitter`
3. **Enrich** — Each chunk gets metadata (filename, page number, chunk index, document ID)
4. **Embed** — Chunks are vectorized using Ollama's `nomic-embed-text` model
5. **Store** — Vectors stored in pgvector; chunk metadata persisted in `document_chunks` table
6. **Search** — User query is embedded, then similarity search against pgvector returns top-k chunks
7. **Generate** — Retrieved chunks are injected as context into the LLM prompt; answer + citations returned
