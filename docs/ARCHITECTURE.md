# FLOXIN NET API Architecture

The API is an adapter around the existing FLOXIN NET project; it does not replace the CLI or rebuild the DNS engine.

`api/server.py` exposes the versioned HTTP and WebSocket routes. `api/auth.py` enforces the generated Bearer token and an in-memory per-token rate limit. `api/config.py` owns the separate JSON configuration and token files. `api/database.py` owns the SQLite schema and query/statistics access. `api/bridge.py` controls the existing DNS process through a PID file and starts it with a loopback bind. Scenario and provider lists are read from the existing shell libraries.

The DNS process continues to own DNS resolution, blocklist matching, caching, and upstream forwarding. It now writes query events to SQLite as well as the legacy text log, so the API does not need to parse a mutable log file for statistics.

The Android client in the next phase will call only `127.0.0.1:8080`, store the token in Android Keystore-backed preferences, and show a Termux/Termux:Boot setup message when the backend is unavailable.
