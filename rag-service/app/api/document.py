from fastapi import APIRouter, Depends, HTTPException
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.exc import IntegrityError
from app.models.schemas import DocumentCreate, DocumentOut
from app.models.db_models import Document as DocModel, KnowledgeBase as KBModel
from app.core.auth import get_current_admin, get_current_user
from app.db.database import get_db
import uuid

router = APIRouter()

@router.get("/knowledge_bases/{kb_id}/documents", response_model=List[DocumentOut])
async def list_documents(kb_id: str, current_user=Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    kb = await db.get(KBModel, uuid.UUID(kb_id))
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    result = await db.execute(select(DocModel).where(DocModel.knowledge_base_id == kb.id))
    docs = result.scalars().all()
    return [DocumentOut(
        id=str(doc.id),
        knowledge_base_id=str(doc.knowledge_base_id),
        title=doc.title,
        source=doc.source,
        status=doc.status,
        created_at=doc.created_at
    ) for doc in docs]

from app.models.db_models import DocumentChunk, Embedding, AIProvider as ProviderModel
from app.core.providers.openai_provider import OpenAIProviderClient
import json

@router.post("/knowledge_bases/{kb_id}/documents", response_model=DocumentOut)
async def add_document(kb_id: str, doc: DocumentCreate, current_admin=Depends(get_current_admin), db: AsyncSession = Depends(get_db)):
    kb = await db.get(KBModel, uuid.UUID(kb_id))
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    # For demo: Accept raw text in 'source' (in future, extract from file)
    if not doc.source:
        raise HTTPException(status_code=400, detail="Document 'source' (raw text) required for ingestion")
    new_doc = DocModel(
        knowledge_base_id=kb.id,
        title=doc.title,
        source=doc.source,
        status=doc.status or "processing"
    )
    db.add(new_doc)
    await db.commit()
    await db.refresh(new_doc)

    # --- Chunking ---
    def simple_chunk(text, size=512, overlap=64):
        words = text.split()
        chunks = []
        i = 0
        while i < len(words):
            chunk = words[i:i+size]
            chunks.append(' '.join(chunk))
            i += size - overlap
        return chunks
    chunks = simple_chunk(doc.source)

    # --- Select embedding provider (OpenAI for now) ---
    provider_id = kb.ai_provider or "openai"
    provider = await db.get(ProviderModel, provider_id)
    if not provider or not provider.enabled:
        raise HTTPException(status_code=400, detail="Embedding provider not found or not enabled")
    if provider.type != "openai":
        raise HTTPException(status_code=400, detail="Only OpenAI embedding supported in this demo")
    embed_client = OpenAIProviderClient(api_key=provider.api_key)
    # --- Generate embeddings ---
    import asyncio
    vectors = await embed_client.embed_texts(chunks)

    # --- Store chunks and embeddings ---
    chunk_objs = []
    for idx, (chunk_text, vector) in enumerate(zip(chunks, vectors)):
        chunk_obj = DocumentChunk(
            document_id=new_doc.id,
            chunk_index=idx,
            text=chunk_text
        )
        db.add(chunk_obj)
        await db.flush()  # Get chunk_obj.id
        emb_obj = Embedding(
            chunk_id=chunk_obj.id,
            provider=provider.id,
            model=embed_client.embedding_model,
            version=None,
            vector=json.dumps(vector)
        )
        db.add(emb_obj)
        chunk_objs.append(chunk_obj)
    new_doc.status = "ready"
    await db.commit()
    await db.refresh(new_doc)
    return DocumentOut(
        id=str(new_doc.id),
        knowledge_base_id=str(new_doc.knowledge_base_id),
        title=new_doc.title,
        source=new_doc.source,
        status=new_doc.status,
        created_at=new_doc.created_at
    )

@router.delete("/documents/{doc_id}")
async def delete_document(doc_id: str, current_admin=Depends(get_current_admin), db: AsyncSession = Depends(get_db)):
    doc = await db.get(DocModel, uuid.UUID(doc_id))
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")
    await db.delete(doc)
    await db.commit()
    return {"detail": "Deleted"}
