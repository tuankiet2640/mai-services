from fastapi import FastAPI
from app.api import knowledge_base, query
from app.api import document
from app.api import ai_provider

app = FastAPI(title="RAG Knowledge Management Service")

# Include routers
def include_routers(app: FastAPI):
    app.include_router(knowledge_base.router, prefix="/knowledge_bases", tags=["KnowledgeBase"])
    app.include_router(document.router, tags=["Document"])
    app.include_router(ai_provider.router, tags=["AIProvider"])
    app.include_router(query.router, prefix="/query", tags=["Query"])

include_routers(app)
