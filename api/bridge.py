from __future__ import annotations

import os
import signal
import subprocess
import time
from pathlib import Path

from .config import DNS_DIR, DNS_PORT, DNS_SCRIPT, ensure_config


class FloxinBridge:
    def __init__(self) -> None:
        self.pid_path = DNS_DIR / ".dns.pid"
        self.started_at_path = DNS_DIR / ".api_started_at"

    def pid(self) -> int | None:
        try:
            pid = int(self.pid_path.read_text().strip())
            os.kill(pid, 0)
            return pid
        except (OSError, ValueError):
            return None

    def status(self) -> dict:
        pid = self.pid()
        started = 0
        if pid and self.started_at_path.exists():
            try:
                started = max(0, int(time.time() - float(self.started_at_path.read_text().strip())))
            except (OSError, ValueError):
                started = 0
        config = ensure_config()
        return {
            "running": pid is not None,
            "pid": pid,
            "mode": config["mode"],
            "provider": config["provider"],
            "uptime_seconds": started,
            "port": DNS_PORT,
        }

    def start(self) -> dict:
        if self.pid():
            return self.status()
        if not DNS_SCRIPT.exists():
            raise FileNotFoundError(f"DNS script not found: {DNS_SCRIPT}")
        DNS_DIR.mkdir(parents=True, exist_ok=True)
        log = DNS_DIR / "dns.log"
        env = os.environ.copy()
        env.setdefault("FLOXIN_PORT", str(DNS_PORT))
        env.setdefault("FLOXIN_BIND_ADDRESS", "127.0.0.1")
        with log.open("ab") as output:
            process = subprocess.Popen(
                ["python3", str(DNS_SCRIPT)],
                cwd=DNS_DIR,
                env=env,
                stdout=output,
                stderr=subprocess.STDOUT,
                start_new_session=True,
            )
        self.pid_path.write_text(str(process.pid) + "\n")
        self.started_at_path.write_text(str(time.time()) + "\n")
        return self.status()

    def stop(self) -> dict:
        pid = self.pid()
        if pid:
            try:
                os.kill(pid, signal.SIGTERM)
            except ProcessLookupError:
                pass
            self.pid_path.unlink(missing_ok=True)
            self.started_at_path.unlink(missing_ok=True)
        return self.status()

    def restart(self) -> dict:
        self.stop()
        return self.start()

    def blocklist_size(self) -> int:
        path = DNS_DIR / "blocklist.txt"
        try:
            return sum(1 for line in path.open(encoding="utf-8", errors="ignore") if line.strip())
        except OSError:
            return 0
