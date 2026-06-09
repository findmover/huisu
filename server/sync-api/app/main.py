from __future__ import annotations

import hashlib
import json
import os
import secrets
import sqlite3
import copy
from contextlib import contextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, Iterator, Optional

from fastapi import Depends, FastAPI, Header, HTTPException, Query, Request, status
from fastapi.responses import JSONResponse
from pydantic import BaseModel, Field


DB_PATH = Path(os.getenv("SYNC_DB_PATH", "/data/sync.db"))
API_TOKEN = os.getenv("SYNC_API_TOKEN", "lee123456")
MAX_BODY_BYTES = int(os.getenv("SYNC_MAX_BODY_BYTES", str(50 * 1024 * 1024)))
HISTORY_LIMIT = int(os.getenv("SYNC_HISTORY_LIMIT", "30"))

SNAPSHOT_ID = "default"
SYNC_TABLES = [
    "meditation_records",
    "affirmation_records",
    "affirmations",
    "video_links",
    "achievements",
    "todo_categories",
    "todo_items",
    "quick_notes",
    "quick_note_images",
]
UPDATED_AT_TABLES = {
    "todo_categories",
    "todo_items",
    "quick_notes",
    "quick_note_images",
}

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


def history_meta(row: sqlite3.Row) -> Dict[str, Any]:
    snapshot_json = row["snapshot_json"]
    return {
        "revision": row["revision"],
        "updated_at": row["updated_at"],
        "updated_by": row["updated_by"],
        "client_updated_at": row["client_updated_at"],
        "content_sha256": row["content_sha256"],
        "stored_bytes": len(snapshot_json.encode("utf-8")),
    }


def to_int(value: Any, default: int = 0) -> int:
    try:
        return int(value)
    except (TypeError, ValueError):
        return default


def snapshot_tables(snapshot: Dict[str, Any]) -> Dict[str, Any]:
    tables = snapshot.get("tables")
    return tables if isinstance(tables, dict) else {}


def snapshot_table_counts(snapshot: Dict[str, Any]) -> Dict[str, int]:
    counts: Dict[str, int] = {}
    for table, rows in snapshot_tables(snapshot).items():
        if isinstance(rows, list):
            counts[table] = len(rows)
    return counts


def row_identity(row: Dict[str, Any]) -> str:
    row_id = to_int(row.get("id"), 0)
    if row_id > 0:
        return f"id:{row_id}"
    return f"hash:{sha256_hex(stable_json(row))}"


def row_equivalence_hash(row: Dict[str, Any]) -> str:
    without_id = {key: value for key, value in row.items() if key != "id"}
    return sha256_hex(stable_json(without_id))


def should_replace_row(
    table: str,
    current: Dict[str, Any],
    incoming: Dict[str, Any],
) -> bool:
    if table in UPDATED_AT_TABLES:
        current_updated_at = to_int(current["row"].get("updatedAt"), -(2**63))
        incoming_updated_at = to_int(incoming["row"].get("updatedAt"), -(2**63))
        if incoming_updated_at != current_updated_at:
            return incoming_updated_at > current_updated_at

    current_rank = (to_int(current["revision"], 0), to_int(current["order"], 0))
    incoming_rank = (to_int(incoming["revision"], 0), to_int(incoming["order"], 0))
    return incoming_rank >= current_rank


def merge_snapshot_values(
    sources: list[tuple[int, Dict[str, Any]]],
) -> tuple[Dict[str, Any], Dict[str, Any]]:
    merged: Dict[str, Dict[str, Dict[str, Any]]] = {}
    source_counts: list[Dict[str, Any]] = []
    schema_version = 1

    for order, (revision, snapshot) in enumerate(sources):
        schema_version = max(schema_version, to_int(snapshot.get("schemaVersion"), 1))
        counts = snapshot_table_counts(snapshot)
        source_counts.append({"revision": revision, "table_counts": counts})

        for table, rows in snapshot_tables(snapshot).items():
            if not isinstance(rows, list):
                continue
            table_rows = merged.setdefault(table, {})

            for raw_row in rows:
                if not isinstance(raw_row, dict):
                    continue
                incoming = {
                    "row": copy.deepcopy(raw_row),
                    "revision": revision,
                    "order": order,
                }
                key = row_identity(incoming["row"])
                current = table_rows.get(key)
                if current is None or should_replace_row(table, current, incoming):
                    table_rows[key] = incoming

    final_tables: Dict[str, list[Dict[str, Any]]] = {}
    table_order = [table for table in SYNC_TABLES if table in merged]
    table_order.extend(sorted(table for table in merged.keys() if table not in SYNC_TABLES))

    for table in table_order:
        equivalent_rows: Dict[str, Dict[str, Any]] = {}
        for item in merged[table].values():
            equivalent_key = row_equivalence_hash(item["row"])
            current = equivalent_rows.get(equivalent_key)
            if current is None or should_replace_row(table, current, item):
                equivalent_rows[equivalent_key] = item

        rows = [item["row"] for item in equivalent_rows.values()]
        rows.sort(
            key=lambda row: (
                to_int(row.get("id"), 0),
                to_int(row.get("updatedAt"), 0),
                stable_json(row),
            )
        )
        final_tables[table] = rows

    merged_snapshot = {
        "schemaVersion": schema_version,
        "exportedAt": int(datetime.now(timezone.utc).timestamp() * 1000),
        "tables": final_tables,
    }
    return merged_snapshot, {
        "source_counts": source_counts,
        "merged_table_counts": snapshot_table_counts(merged_snapshot),
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


@app.get("/v1/sync/history", dependencies=[Depends(require_auth)])
def get_history(limit: int = Query(default=HISTORY_LIMIT, ge=1, le=200)) -> Dict[str, Any]:
    with connect_db() as db:
        rows = db.execute(
            """
            SELECT *
            FROM sync_snapshot_history
            ORDER BY id DESC
            LIMIT ?
            """,
            (limit,),
        ).fetchall()
        return {"items": [history_meta(row) for row in rows]}


@app.get("/v1/sync/history/{revision}", dependencies=[Depends(require_auth)])
def get_history_snapshot(revision: int) -> Dict[str, Any]:
    with connect_db() as db:
        row = db.execute(
            """
            SELECT *
            FROM sync_snapshot_history
            WHERE revision = ?
            ORDER BY id DESC
            LIMIT 1
            """,
            (revision,),
        ).fetchone()
        if row is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="history revision not found",
            )
        return snapshot_response(row)


@app.post("/v1/sync/restore/{revision}", dependencies=[Depends(require_auth)])
def restore_history_snapshot(
    revision: int,
    device_id: str = Query(default="server-restore", min_length=1, max_length=128),
) -> Dict[str, Any]:
    updated_at = utc_now()

    with connect_db() as db:
        history = db.execute(
            """
            SELECT *
            FROM sync_snapshot_history
            WHERE revision = ?
            ORDER BY id DESC
            LIMIT 1
            """,
            (revision,),
        ).fetchone()
        if history is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="history revision not found",
            )

        current = fetch_current(db)
        current_revision = int(current["revision"]) if current is not None else 0

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
        restore_actor = f"restore:{device_id}"
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
                history["snapshot_json"],
                updated_at,
                restore_actor,
                history["client_updated_at"],
                history["content_sha256"],
            ),
        )
        prune_history(db)

    return {
        "restored": True,
        "restored_from_revision": revision,
        "revision": next_revision,
        "updated_at": updated_at,
        "updated_by": restore_actor,
        "content_sha256": history["content_sha256"],
        "stored_bytes": len(history["snapshot_json"].encode("utf-8")),
    }


@app.post("/v1/sync/merge-history", dependencies=[Depends(require_auth)])
def merge_history_snapshots(
    limit: int = Query(default=HISTORY_LIMIT, ge=1, le=200),
    device_id: str = Query(default="server-merge", min_length=1, max_length=128),
    dry_run: bool = Query(default=False),
) -> Dict[str, Any]:
    updated_at = utc_now()

    with connect_db() as db:
        current = fetch_current(db)
        if current is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="current snapshot not found",
            )

        history_rows = db.execute(
            """
            SELECT *
            FROM (
                SELECT *
                FROM sync_snapshot_history
                ORDER BY id DESC
                LIMIT ?
            )
            ORDER BY id ASC
            """,
            (limit,),
        ).fetchall()

        sources = [
            (to_int(row["revision"]), json.loads(row["snapshot_json"]))
            for row in history_rows
        ]
        sources.append((to_int(current["revision"]), json.loads(current["snapshot_json"])))

        merged_snapshot, summary = merge_snapshot_values(sources)
        if not snapshot_tables(merged_snapshot):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="no mergeable snapshot tables found",
            )

        snapshot_json = stable_json(merged_snapshot)
        content_hash = sha256_hex(snapshot_json)
        source_revisions = [revision for revision, _snapshot in sources]
        stored_bytes = len(snapshot_json.encode("utf-8"))

        if dry_run:
            return {
                "merged": False,
                "dry_run": True,
                "source_revisions": source_revisions,
                "merged_table_counts": summary["merged_table_counts"],
                "source_counts": summary["source_counts"],
                "content_sha256": content_hash,
                "stored_bytes": stored_bytes,
            }

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

        next_revision = to_int(current["revision"]) + 1
        merge_actor = f"merge:{device_id}"
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
                merge_actor,
                str(merged_snapshot["exportedAt"]),
                content_hash,
            ),
        )
        prune_history(db)

    return {
        "merged": True,
        "revision": next_revision,
        "updated_at": updated_at,
        "updated_by": merge_actor,
        "source_revisions": source_revisions,
        "merged_table_counts": summary["merged_table_counts"],
        "source_counts": summary["source_counts"],
        "content_sha256": content_hash,
        "stored_bytes": stored_bytes,
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
