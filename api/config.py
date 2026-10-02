from __future__ import annotations

import json
import os
import secrets
from pathlib import Path
from typing import Any

CONFIG_DIR = Path(os.environ.get("FLOXIN_CONFIG_DIR", Path.home() / ".config" / "floxin"))
CONFIG_PATH = Path(os.environ.get("FLOXIN_CONFIG_PATH", CONFIG_DIR / "config.json"))
TOKEN_PATH = Path(os.environ.get("FLOXIN_TOKEN_PATH", CONFIG_DIR / "token"))
DB_PATH = Path(os.environ.get("FLOXIN_DB_PATH", CONFIG_DIR / "stats.db"))
DNS_DIR = Path(os.environ.get("FLOXIN_DNS_DIR", Path.home() / "dnsmasq"))
DNS_SCRIPT = Path(os.environ.get("FLOXIN_DNS_SCRIPT", DNS_DIR / "floxin_dns.py"))
API_HOST = os.environ.get("FLOXIN_API_HOST", "127.0.0.1")
API_PORT = int(os.environ.get("FLOXIN_API_PORT", "8080"))
DNS_PORT = int(os.environ.get("FLOXIN_PORT", "5353"))

DEFAULT_CONFIG: dict[str, Any] = {
    "mode": "DATA_SAVER",
    "network": "AUTO",
    "provider": "Cloudflare",
    "cache_ttl": 600,
    "logging": True,
    "theme": "dark",
}


def ensure_config() -> dict[str, Any]:
    CONFIG_DIR.mkdir(parents=True, exist_ok=True)
    if not CONFIG_PATH.exists():
        CONFIG_PATH.write_text(json.dumps(DEFAULT_CONFIG, indent=2) + "\n", encoding="utf-8")
    try:
        value = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        value = {}
    merged = {**DEFAULT_CONFIG, **value}
    CONFIG_PATH.write_text(json.dumps(merged, indent=2) + "\n", encoding="utf-8")
    return merged


def save_config(updates: dict[str, Any]) -> dict[str, Any]:
    current = ensure_config()
    current.update(updates)
    CONFIG_PATH.write_text(json.dumps(current, indent=2) + "\n", encoding="utf-8")
    return current


def ensure_token() -> str:
    CONFIG_DIR.mkdir(parents=True, exist_ok=True)
    if TOKEN_PATH.exists():
        token = TOKEN_PATH.read_text(encoding="utf-8").strip()
        if token:
            return token
    token = secrets.token_urlsafe(32)
    TOKEN_PATH.write_text(token + "\n", encoding="utf-8")
    try:
        TOKEN_PATH.chmod(0o600)
    except OSError:
        pass
    return token
