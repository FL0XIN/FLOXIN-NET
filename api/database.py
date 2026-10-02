from __future__ import annotations

import sqlite3
from contextlib import contextmanager
from pathlib import Path
from typing import Iterator

SCHEMA = """
CREATE TABLE IF NOT EXISTS queries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp TEXT NOT NULL DEFAULT (datetime('now')),
    qtype TEXT NOT NULL DEFAULT '',
    domain TEXT NOT NULL,
    action TEXT NOT NULL,
    result TEXT NOT NULL DEFAULT ''
);
CREATE INDEX IF NOT EXISTS idx_queries_timestamp ON queries(timestamp);
CREATE INDEX IF NOT EXISTS idx_queries_action ON queries(action);
"""


class Database:
    def __init__(self, path: Path):
        self.path = path
        self.path.parent.mkdir(parents=True, exist_ok=True)
        self.init()

    def connect(self) -> sqlite3.Connection:
        conn = sqlite3.connect(self.path, timeout=5)
        conn.row_factory = sqlite3.Row
        return conn

    def init(self) -> None:
        with self.connect() as conn:
            conn.executescript(SCHEMA)

    def add_query(self, qtype: str, domain: str, action: str, result: str = "") -> None:
        with self.connect() as conn:
            conn.execute(
                "INSERT INTO queries(qtype, domain, action, result) VALUES (?, ?, ?, ?)",
                (qtype, domain, action, result),
            )

    def stats(self, blocklist_size: int = 0) -> dict[str, int]:
        with self.connect() as conn:
            row = conn.execute(
                """SELECT
                   COUNT(*) AS total,
                   COALESCE(SUM(action='BLOCK'), 0) AS blocked,
                   COALESCE(SUM(action='CACHE'), 0) AS cached,
                   COALESCE(SUM(action='OK'), 0) AS forwarded
                   FROM queries"""
            ).fetchone()
            cache_size = conn.execute(
                "SELECT COUNT(DISTINCT domain || '|' || qtype) FROM queries WHERE action='CACHE'"
            ).fetchone()[0]
        return {
            "total_queries": int(row["total"]),
            "blocked": int(row["blocked"]),
            "cached": int(row["cached"]),
            "forwarded": int(row["forwarded"]),
            "blocklist_size": int(blocklist_size),
            "cache_size": int(cache_size),
        }

    def recent(self, limit: int = 50) -> list[dict[str, str]]:
        limit = max(1, min(limit, 500))
        with self.connect() as conn:
            rows = conn.execute(
                "SELECT timestamp, domain, action, qtype, result FROM queries ORDER BY id DESC LIMIT ?",
                (limit,),
            ).fetchall()
        return [dict(row) for row in rows]
