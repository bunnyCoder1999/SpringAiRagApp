# Document Intelligence & Semantic Search Engine (RAG)

An enterprise **"Chat with your Documents"** platform for HR policies, financial reports, or technical documentation using **Retrieval-Augmented Generation (RAG)**.

Users upload PDFs, the service parses them, splits them into chunks, converts them into vector embeddings, and stores them in a vector database. 

Users can then ask natural language questions grounded purely in those documents.

## Key Features

- **Ingestion Pipeline** — Parse PDF/Word files using Apache Tika
- **Vectorization** — Generate embeddings using Ollama (running local models like Llama 3)
- **Semantic Search** — Store and query embeddings in a pgvector database
- **Source Citations** — Returns the specific file name and page number where it found the answer

## Tech Stack

- Spring Boot, Spring AI
- Vector Database: pgvector (PostgreSQL extension)
- Local LLM: Ollama

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven (or use the included `mvnw` wrapper)

## Getting Started

### 1. Start Infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL with pgvector and Ollama with the required models.

### 2. Configure Environment

Create a `.env` file in the project root with your database credentials:

```bash
# .env
POSTGRES_DB=<your-database-name>
POSTGRES_USER=<your-db-user>
POSTGRES_PASSWORD=<your-db-password>
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

## API Usage

### Upload a Document

```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@document.pdf"
```

### Ask a Question

```bash
curl -X POST http://localhost:8080/api/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "Your question here"}'
```
