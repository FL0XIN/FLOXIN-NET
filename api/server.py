from __future__ import annotations

import platform
import re
import time
import os
from pathlib import Path

from fastapi import Depends, FastAPI, HTTPException, Query, WebSocket, WebSocketDisconnect
from pydantic import BaseModel, Field

from .auth import require_token
from .bridge import FloxinBridge
from .config import DB_PATH, DNS_DIR, ensure_config, ensure_token, save_config
from .database import Database

app = FastAPI(title="FLOXIN NET API", version="1.0.0", docs_url="/docs", redoc_url="/redoc")
db = Database(DB_PATH)
bridge = FloxinBridge()
_started_at = time.monotonic()

class ScenarioRequest(BaseModel):
    code: str = Field(min_length=1, max_length=64)

class ProviderRequest(BaseModel):
    name: str = Field(min_length=1, max_length=128)

class ConfigRequest(BaseModel):
    mode: str | None = None
    network: str | None = None
    provider: str | None = None
    cache_ttl: int | None = Field(default=None, ge=0, le=86400)
    logging: bool | None = None
    theme: str | None = None


PROJECT_ROOT = Path(os.environ.get("FLOXIN_PROJECT_ROOT", Path(__file__).parents[1]))


def _entries(path: Path, marker: str) -> list[dict[str, str]]:
    if not path.exists():
        return []
    result = []
    pattern = re.compile(r'"([^"\n]+)"')
    for line in path.read_text(encoding="utf-8", errors="ignore").splitlines():
        if marker not in line or '|' not in line:
            continue
        match = pattern.search(line)
        if not match:
            continue
        fields = match.group(1).split("|")
        if marker == "SCENARIO_CODES":
            if len(fields) >= 7:
                result.append({"code": fields[0], "type": fields[1], "provider": fields[2], "network": fields[3], "vpn": fields[4], "description": fields[6]})
        else:
            if len(fields) >= 4:
                result.append({"name": fields[0], "primary": fields[1], "secondary": fields[2], "region": fields[3]})
    return result


def _find_scenario(code: str) -> dict | None:
    for item in _entries(PROJECT_ROOT / "lib" / ".floxin_scenarios.sh", "SCENARIO_CODES"):
        if item["code"].upper() == code.upper():
            return item
    return None


def _find_provider(name: str) -> dict | None:
    for item in _entries(PROJECT_ROOT / "lib" / ".floxin_codes.sh", "EXTENDED_PROVIDERS"):
        if item["name"].lower() == name.lower():
            return item
    return None

@app.get("/api/v1/status")
def status(_: str = Depends(require_token)):
    return bridge.status()

@app.get("/api/v1/stats")
def stats(_: str = Depends(require_token)):
    return db.stats(bridge.blocklist_size())

@app.post("/api/v1/start")
def start(_: str = Depends(require_token)):
    try:
        return {"ok": True, "status": bridge.start()}
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

@app.post("/api/v1/stop")
def stop(_: str = Depends(require_token)):
    return {"ok": True, "status": bridge.stop()}

@app.post("/api/v1/restart")
def restart(_: str = Depends(require_token)):
    try:
        return {"ok": True, "status": bridge.restart()}
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

@app.get("/api/v1/scenarios")
def scenarios(_: str = Depends(require_token)):
    return _entries(PROJECT_ROOT / "lib" / ".floxin_scenarios.sh", "SCENARIO_CODES")

@app.post("/api/v1/scenario")
def scenario(payload: ScenarioRequest, _: str = Depends(require_token)):
    item = _find_scenario(payload.code)
    if not item:
        raise HTTPException(status_code=404, detail="Scenario not found")
    save_config({"mode": item["type"], "network": item["network"], "provider": item["provider"]})
    return {"ok": True, "message": "Applied", "scenario": item}

@app.get("/api/v1/providers")
def providers(_: str = Depends(require_token)):
    return _entries(PROJECT_ROOT / "lib" / ".floxin_codes.sh", "EXTENDED_PROVIDERS")

@app.post("/api/v1/provider")
def provider(payload: ProviderRequest, _: str = Depends(require_token)):
    item = _find_provider(payload.name)
    if not item:
        raise HTTPException(status_code=404, detail="Provider not found")
    return {"ok": True, "config": save_config({"provider": item["name"]})}

@app.get("/api/v1/queries")
def queries(limit: int = Query(default=50, ge=1, le=500), _: str = Depends(require_token)):
    return db.recent(limit)

@app.get("/api/v1/blocklist")
def blocklist(_: str = Depends(require_token)):
    path = DNS_DIR / "blocklist.txt"
    return {"path": str(path), "exists": path.exists(), "size": bridge.blocklist_size()}

@app.get("/api/v1/doctor")
def doctor(_: str = Depends(require_token)):
    checks = {"python": bool(platform.python_version()), "dns_script": bridge.pid() is not None or bridge.status(), "database": DB_PATH.exists()}
    return {"ok": all(bool(value) for value in checks.values()), "checks": checks}

@app.get("/api/v1/env")
def env(_: str = Depends(require_token)):
    return {"python": platform.python_version(), "system": platform.system(), "release": platform.release(), "machine": platform.machine(), "api_uptime_seconds": int(time.monotonic() - _started_at)}

@app.get("/api/v1/config")
def get_config(_: str = Depends(require_token)):
    return ensure_config()

@app.post("/api/v1/config")
def update_config(payload: ConfigRequest, _: str = Depends(require_token)):
    values = payload.model_dump(exclude_none=True)
    return {"ok": True, "config": save_config(values)}

@app.websocket("/api/v1/ws")
async def websocket(websocket: WebSocket):
    auth = websocket.headers.get("authorization", "")
    if auth != f"Bearer {ensure_token()}":
        await websocket.close(code=1008)
        return
    await websocket.accept()
    try:
        while True:
            await websocket.send_json({"type": "status", "data": bridge.status()})
            await websocket.receive_text()
    except WebSocketDisconnect:
        return

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("api.server:app", host="127.0.0.1", port=8080)
