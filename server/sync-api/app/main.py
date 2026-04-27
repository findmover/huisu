from __future__ import annotations

import hashlib
import json
import os
import secrets
import sqlite3
from contextlib import contextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, Iterator, Optional

from fastapi import Depends, FastAPI, Header, HTTPException, Query, Request, status
from fastapi.responses import JSONResponse
from pydantic import BaseModel, Field


DB_PATH = Path(os.getenv("SYNC_DB_PATH", "/data/sync.db"))
API_TOKEN = os.getenv("SYNC_API_TOKEN", "lee123456")
MAX_BODY_BYTES = int(os.getenv("SYNC_MAX_BODY_BYTES", str(10 * 1024 * 1024)))
HISTORY_LIMIT = int(os.getenv("SYNC_HISTORY_LIMIT", "30"))

SNAPSHOT_ID = "default"

app = FastAPI(
    title="HuiSu Sync API",
    version="1.0.0",
    docs_url="/docs",
    redoc_url=None,
)


class SnapshotUpload(BaseModel):
    device_id: str = Field(min_length=1, max_length=128)
    base_revision: int = Field(ge=0)
    snapshot: Dict[str, Any]
    client_updated_at: Optional[str] = Field(default=None, max_length=64)


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def stable_json(value: dict[str, Any]) -> str:
    return json.dumps(value, ensure_ascii=False, sort_keys=True, separators=(",", ":"))


def sha256_hex(value: str) -> str:
    return hashlib.sha256(value.encode("utf-8")).hexdigest()


@contextmanager
def connect_db() -> Iterator[sqlite3.Connection]:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    connection = sqlite3.connect(DB_PATH)
    connection.row_factory = sqlite3.Row
    try:
        yield connection
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()


def init_db() -> None:
    with connect_db() as db:
        db.execute(
            """
            CREATE TABLE IF NOT EXISTS sync_snapshot (
                id TEXT PRIMARY KEY,
                revision INTEGER NOT NULL,
                snapshot_json TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                updated_by TEXT NOT NULL,
                client_updated_at TEXT,
                content_sha256 TEXT NOT NULL
            )
            """
        )
        db.execute(
            """
            CREATE TABLE IF NOT EXISTS sync_snapshot_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                revision INTEGER NOT NULL,
                snapshot_json TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                updated_by TEXT NOT NULL,
                client_updated_at TEXT,
                content_sha256 TEXT NOT NULL
            )
            """
        )


@app.on_event("startup")
def on_startup() -> None:
    init_db()


@app.middleware("http")
async def limit_request_body(request: Request, call_next):
    content_length = request.headers.get("content-length")
    if content_length and int(content_length) > MAX_BODY_BYTES:
        return JSONResponse(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            content={"detail": "request body too large"},
        )
    return await call_next(request)


def require_auth(authorization: Optional[str] = Header(default=None)) -> None:
    if not API_TOKEN:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="SYNC_API_TOKEN is not configured",
        )
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="missing bearer token",
            headers={"WWW-Authenticate": "Bearer"},
        )
    token = authorization[len("Bearer ") :].strip()
    if not secrets.compare_digest(token, API_TOKEN):
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="invalid token")


def fetch_current(db: sqlite3.Connection) -> Optional[sqlite3.Row]:
    return db.execute(
        "SELECT * FROM sync_snapshot WHERE id = ?",
        (SNAPSHOT_ID,),
    ).fetchone()


def snapshot_response(row: Optional[sqlite3.Row]) -> Dict[str, Any]:
    if row is None:
        return {
            "exists": False,
            "revision": 0,
            "updated_at": None,
            "updated_by": None,
            "client_updated_at": None,
            "content_sha256": None,
            "snapshot": None,
        }

    return {
        "exists": True,
        "revision": row["revision"],
        "updated_at": row["updated_at"],
        "updated_by": row["updated_by"],
        "client_updated_at": row["client_updated_at"],
        "content_sha256": row["content_sha256"],
        "snapshot": json.loads(row["snapshot_json"]),
    }


def prune_history(db: sqlite3.Connection) -> None:
    db.execute(
        """
        DELETE FROM sync_snapshot_history
        WHERE id NOT IN (
            SELECT id FROM sync_snapshot_history
            ORDER BY id DESC
            LIMIT ?
        )
        """,
        (HISTORY_LIMIT,),
    )


@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok", "time": utc_now()}


@app.post("/v1/sync/probe", dependencies=[Depends(require_auth)])
def probe() -> Dict[str, str]:
    return {"status": "ok", "time": utc_now()}


@app.get("/v1/sync/snapshot", dependencies=[Depends(require_auth)])
def get_snapshot() -> Dict[str, Any]:
    with connect_db() as db:
        return snapshot_response(fetch_current(db))


@app.get("/v1/sync/meta", dependencies=[Depends(require_auth)])
def get_meta() -> Dict[str, Any]:
    with connect_db() as db:
        row = fetch_current(db)
        if row is None:
            return {"exists": False, "revision": 0}
        return {
            "exists": True,
            "revision": row["revision"],
            "updated_at": row["updated_at"],
            "updated_by": row["updated_by"],
            "client_updated_at": row["client_updated_at"],
            "content_sha256": row["content_sha256"],
        }


@app.put("/v1/sync/snapshot", dependencies=[Depends(require_auth)])
def put_snapshot(
    payload: SnapshotUpload,
    force: bool = Query(default=False),
) -> Dict[str, Any]:
    snapshot_json = stable_json(payload.snapshot)
    content_hash = sha256_hex(snapshot_json)
    updated_at = utc_now()

    with connect_db() as db:
        current = fetch_current(db)
        current_revision = int(current["revision"]) if current is not None else 0

        if not force and payload.base_revision != current_revision:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail={
                    "message": "revision conflict",
                    "current_revision": current_revision,
                    "updated_at": current["updated_at"] if current is not None else None,
                    "updated_by": current["updated_by"] if current is not None else None,
                },
            )

        if current is not None:
            db.execute(
                """
                INSERT INTO sync_snapshot_history (
                    revision,
                    snapshot_json,
                    updated_at,
                    updated_by,
                    client_updated_at,
                    content_sha256
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                (
                    current["revision"],
                    current["snapshot_json"],
                    current["updated_at"],
                    current["updated_by"],
                    current["client_updated_at"],
                    current["content_sha256"],
                ),
            )

        next_revision = current_revision + 1
        db.execute(
            """
            INSERT INTO sync_snapshot (
                id,
                revision,
                snapshot_json,
                updated_at,
                updated_by,
                client_updated_at,
                content_sha256
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                revision = excluded.revision,
                snapshot_json = excluded.snapshot_json,
                updated_at = excluded.updated_at,
                updated_by = excluded.updated_by,
                client_updated_at = excluded.client_updated_at,
                content_sha256 = excluded.content_sha256
            """,
            (
                SNAPSHOT_ID,
                next_revision,
                snapshot_json,
                updated_at,
                payload.device_id,
                payload.client_updated_at,
                content_hash,
            ),
        )
        prune_history(db)

    return {
        "stored": True,
        "revision": next_revision,
        "updated_at": updated_at,
        "updated_by": payload.device_id,
        "content_sha256": content_hash,
        "stored_bytes": len(snapshot_json.encode("utf-8")),
    }
