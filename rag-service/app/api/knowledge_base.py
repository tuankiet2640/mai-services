from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File, Request
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.exc import IntegrityError
from app.models.schemas import KnowledgeBaseCreate, KnowledgeBaseOut
from app.models.db_models import KnowledgeBase as KBModel
from app.core.auth import get_current_admin, get_current_user
from app.db.database import get_db
import uuid
from app.services.rag import get_rag_service

router = APIRouter()

@router.get("/", response_model=List[KnowledgeBaseOut])
async def list_knowledge_bases(current_user=Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(KBModel))
    kbs = result.scalars().all()
    return [KnowledgeBaseOut(
        id=str(kb.id),
        name=kb.name,
        description=kb.description,
        ai_provider=kb.ai_provider
    ) for kb in kbs]

@router.post("/{kb_id}/ingest")
async def ingest_documents(
    kb_id: str,
    files: List[UploadFile] = File(...),
    current_admin=Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    """Ingest documents into a knowledge base. Only accessible by admins."""
    kb = await db.get(KBModel, uuid.UUID(kb_id))
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    rag_service = get_rag_service(db)
    results = []
    for file in files:
        content = await file.read()
        res = await rag_service.ingest_document(kb, content, filename=file.filename)
        results.append(res)
    return {"status": "success", "details": results}

@router.post("/{kb_id}/chat")
async def chat(
    kb_id: str,
    request: Request,
    current_user=Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Query the RAG pipeline for a KB. Authenticated users only."""
    data = await request.json()
    query = data.get("query")
    if not query:
        raise HTTPException(status_code=400, detail="Missing query in request body")
    kb = await db.get(KBModel, uuid.UUID(kb_id))
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    rag_service = get_rag_service(db)
    response = await rag_service.query(kb, query)
    return {"response": response}

@router.get("/health")
async def health():
    return {"status": "ok"}

@router.post("/", response_model=KnowledgeBaseOut)
async def create_knowledge_base(kb: KnowledgeBaseCreate, current_admin=Depends(get_current_admin), db: AsyncSession = Depends(get_db)):
    new_kb = KBModel(
        name=kb.name,
        description=kb.description,
        ai_provider=kb.ai_provider
    )
    db.add(new_kb)
    try:
        await db.commit()
        await db.refresh(new_kb)
    except IntegrityError:
        await db.rollback()
        raise HTTPException(status_code=400, detail="Knowledge base already exists")
    return KnowledgeBaseOut(
        id=str(new_kb.id),
        name=new_kb.name,
        description=new_kb.description,
        ai_provider=new_kb.ai_provider
    )

@router.delete("/{kb_id}")
async def delete_knowledge_base(kb_id: str, current_admin=Depends(get_current_admin), db: AsyncSession = Depends(get_db)):
    kb = await db.get(KBModel, uuid.UUID(kb_id))
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    await db.delete(kb)
    await db.commit()
    return {"detail": "Deleted"}
