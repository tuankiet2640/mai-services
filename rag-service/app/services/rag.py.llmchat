import uuid
from typing import Any
from app.models.db_models import KnowledgeBase as KBModel, Document as DocModel, DocumentChunk, Embedding, AIProvider as ProviderModel, Document
from app.core.providers.openai_provider import OpenAIProviderClient
import json

# You can expand this to support more providers and vector DBs.

class RAGService:
    def __init__(self, db):
        self.db = db

    async def ingest_document(self, kb: KBModel, content: bytes, filename: str = None) -> dict:
        text = content.decode(errors="ignore")
        new_doc = DocModel(
            knowledge_base_id=kb.id,
            title=filename or "Untitled",
            source=text,
            status="processing"
        )
        self.db.add(new_doc)
        await self.db.commit()
        await self.db.refresh(new_doc)

        def simple_chunk(text, size=512, overlap=64):
            words = text.split()
            chunks = []
            i = 0
            while i < len(words):
                chunk = words[i:i+size]
                chunks.append(' '.join(chunk))
                i += size - overlap
            return chunks

        chunks = simple_chunk(text)
        provider_id = kb.ai_provider or "openai"
        provider = await self.db.get(ProviderModel, provider_id)
        if not provider or not provider.enabled:
            raise Exception("AI provider not found or not enabled")
        embed_client = OpenAIProviderClient(api_key=provider.api_key, embedding_model=json.loads(provider.config_json).get("embedding_model", "text-embedding-ada-002"))
        vectors = await embed_client.embed_texts(chunks)

        chunk_objs = []
        for idx, (chunk_text, vector) in enumerate(zip(chunks, vectors)):
            chunk_obj = DocumentChunk(
                document_id=new_doc.id,
                chunk_index=idx,
                text=chunk_text
            )
            self.db.add(chunk_obj)
            await self.db.flush()  # Get chunk_obj.id
            emb_obj = Embedding(
                chunk_id=chunk_obj.id,
                provider=provider.id,
                model=embed_client.embedding_model,
                version=None,
                vector=json.dumps(vector)
            )
            self.db.add(emb_obj)
            chunk_objs.append(chunk_obj)
        new_doc.status = "ready"
        await self.db.commit()
        await self.db.refresh(new_doc)
        return {
            "document_id": str(new_doc.id),
            "chunks": len(chunk_objs),
            "status": new_doc.status
        }

    async def query(self, kb: KBModel, query: str) -> Any:
        provider_id = kb.ai_provider or "openai"
        provider = await self.db.get(ProviderModel, provider_id)
        if not provider or not provider.enabled:
            raise Exception("AI provider not found or not enabled")
        if provider.type != "openai":
            raise Exception("Only OpenAI supported in this demo")
        embed_client = OpenAIProviderClient(api_key=provider.api_key)
        from sqlalchemy.future import select
        from app.models.db_models import Document, DocumentChunk
        result = await self.db.execute(
            select(DocumentChunk)
            .join(Document, DocumentChunk.document_id == Document.id)
            .where(Document.knowledge_base_id == kb.id)
        )
        chunks = result.scalars().all()
        if not chunks:
            return {"answer": "No data found in knowledge base."}
        # --- REAL LLM-POWERED ANSWER ---
        context = "\n".join(chunk.text for chunk in chunks[:3])
        prompt = (
            f"Use the following context to answer the question.\n"
            f"Context:\n{context}\n"
            f"Question: {query}\n"
            "Answer:"
        )
        answer = await embed_client.complete(prompt)
        return {"answer": answer, "context": context}

def get_rag_service(db):
    return RAGService(db)
