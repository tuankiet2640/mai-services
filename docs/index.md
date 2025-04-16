# MAI Platform – Master Documentation

## Overview

The MAI Platform is a modular AI-powered chat and knowledge management system, consisting of:

- **mai-app**: The frontend React application for chat and user interaction.
- **mai-services**: The backend Spring Boot service for authentication, user/role management, and API.
- **rag-service**: The RAG (Retrieval-Augmented Generation) microservice for knowledge base and AI-powered query handling.

---

## Architecture Diagram

```plaintext
[mai-app (React)] <--> [mai-services (Spring Boot API)] <--> [rag-service (FastAPI)]
```

---

## Subproject Summaries

### 1. mai-app (Frontend)

- **Location:** `mai-app/`
- **Tech Stack:** React 18, TailwindCSS, Redux Toolkit, React Router v6, Axios, Socket.IO Client
- **Features:**
  - Modern chat UI with dark/light theme
  - User authentication (username/password, JWT)
  - Real-time chat via WebSockets
  - Data visualization (Recharts)
- **Dev Scripts:**
  - `npm start` – Start dev server
  - `npm test` – Run tests
  - `npm run build` – Production build


### 2. mai-services (Backend API)

- **Location:** `mai-services/`
- **Tech Stack:** Java 21+, Spring Boot, Spring Security, JWT, PostgreSQL
- **Features:**
  - User authentication (JWT, access/refresh tokens)
  - Role and permission management
  - REST API for users, roles, permissions
  - Integrates with mai-app frontend and rag-service
- **Endpoints:** `/api/auth`, `/api/users`, `/api/roles`, `/api/permissions`
- **Default Admin:** Username: `admin`, Password: `admin123`
- **Docs:** See [mai-services/README.md](../mai-services/README.md)

### 3. rag-service (RAG Microservice)

- **Location:** `rag-service/`
- **Tech Stack:** Python, FastAPI
- **Features:**
  - Knowledge base management
  - AI-powered query endpoint
  - Document ingestion and retrieval
- **Endpoints:** `/knowledge_bases`, `/query`, `/document`, `/ai_provider`
- **Docs:** See code in [rag-service/app/](../rag-service/app/) (add a README for more details)

---

## Integration Flow

1. **User logs in** via mai-app (frontend).
2. **mai-app** authenticates with **mai-services** (`/auth/login`), receives JWT tokens and user info.
3. **mai-app** interacts with chat and knowledge features, sending/receiving data via **mai-services**.
4. **mai-services** may proxy or coordinate with **rag-service** for advanced AI and retrieval-augmented queries.

---

## Development Setup

### Prerequisites

- Node.js (for mai-app)
- Java 21+, Maven, PostgreSQL (for mai-services)
- Python 3.9+, pip (for rag-service)

### Quickstart

```bash
# 1. Start mai-services (Spring Boot API)
cd mai-services
mvn spring-boot:run

# 2. Start rag-service (FastAPI)
cd rag-service
pip install -r requirements.txt
uvicorn app.main:app --reload

# 3. Start mai-app (React frontend)
cd mai-app
npm install
npm start
```

---

## Contributing

See individual READMEs for contribution guidelines.
