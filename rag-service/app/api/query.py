from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from app.models.schemas import QueryRequest, QueryResponse
from app.models.db_models import KnowledgeBase as KBModel, AIProvider as ProviderModel
from app.core.auth import get_current_user
from app.db.database import get_db
from app.core.ai_provider import dispatch_query
import uuid

router = APIRouter()

@router.post("/", response_model=QueryResponse)
async def query_knowledge_base(request: QueryRequest, current_user=Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    # Validate knowledge base
    try:
        kb_uuid = uuid.UUID(request.knowledge_base_id)
    except Exception:
        raise HTTPException(status_code=400, detail="Invalid knowledge_base_id format")
    kb = await db.get(KBModel, kb_uuid)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")

    # Determine provider: request > kb default > error
    provider_id = request.ai_provider or kb.ai_provider
    if not provider_id:
        raise HTTPException(status_code=400, detail="No AI provider specified for this knowledge base or request")
    provider = await db.get(ProviderModel, provider_id)
    if not provider or not provider.enabled:
        raise HTTPException(status_code=404, detail="AI provider not found or not enabled")

    # Route query to provider (stub)
    return dispatch_query(request, provider=provider)
