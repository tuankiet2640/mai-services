# RAG Knowledge Management Service

A Retrieval-Augmented Generation (RAG) microservice for document ingestion, semantic search, and LLM-powered Q&A, secured with centralized authentication via mai-services.

---

## **Authentication**
- All protected endpoints require a valid JWT from mai-services in the `Authorization: Bearer <token>` header.
- Tokens are validated by calling mai-services `/api/v1/auth/validate-token`.

---

## **Endpoints & Example Requests**

### 1. **List Knowledge Bases**
- **GET** `/knowledge_bases/`
- **Headers:** `Authorization: Bearer <token>`

```
curl -X GET http://localhost:8000/knowledge_bases/ \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

### 2. **Create Knowledge Base**
- **POST** `/knowledge_bases/`
- **Headers:** `Authorization: Bearer <token>`
- **Body:**
```json
{
  "name": "My KB",
  "description": "Test KB",
  "ai_provider": "openai"
}
```

```
curl -X POST http://localhost:8000/knowledge_bases/ \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name": "My KB", "description": "Test KB", "ai_provider": "openai"}'
```

---

### 3. **Ingest Documents (Admin Only)**
- **POST** `/knowledge_bases/{kb_id}/ingest`
- **Headers:** `Authorization: Bearer <token>`
- **Body:** Multipart file upload

```
curl -X POST "http://localhost:8000/knowledge_bases/<KB_ID>/ingest" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -F "files=@/path/to/your/document.txt"
```

---

### 4. **Query Knowledge Base**
- **POST** `/knowledge_bases/{kb_id}/chat`
- **Headers:** `Authorization: Bearer <token>`
- **Body:**
```json
{
  "query": "What is this document about?"
}
```

```
curl -X POST "http://localhost:8000/knowledge_bases/<KB_ID>/chat" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"query": "What is this document about?"}'
```

---

### 5. **Health Check**
- **GET** `/knowledge_bases/health`

```
curl http://localhost:8000/knowledge_bases/health
```

---

## **Setup**

1. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```
2. **Configure environment:**
   - Set up `.env` with your DB and AI provider credentials.
   - Ensure mai-services is running and accessible.
3. **Run the service:**
   ```bash
   uvicorn app.main:app --reload
   ```

---

## **Notes**
- Only users with `ADMIN` role (from mai-services) can ingest documents.
- All endpoints expect JWTs issued by mai-services.
- Vector search and LLM response are demo/stub; integrate with your preferred vector DB and LLM as needed.

---

## **License**
MIT
