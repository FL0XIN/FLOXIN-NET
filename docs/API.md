# FLOXIN NET REST API

The API is a localhost-only FastAPI service on `127.0.0.1:8080`. Every HTTP endpoint requires `Authorization: Bearer <token>`. The token is generated on first start and stored at `~/.config/floxin/token` with file mode `0600`.

## Run

```bash
python3 -m pip install -r api/requirements.txt
FLOXIN api start
```

The installed `FLOXIN api` command supports `start`, `stop`, `restart`, `status`, and `log`. It uses `~/.floxin/api.pid` and `~/.floxin/api.log`; the API runs in the background and is stopped without broad process matching.

The service stores configuration in `~/.config/floxin/config.json` and query statistics in `~/.config/floxin/stats.db`. The DNS engine writes each query to SQLite and retains the legacy `queries.log` for compatibility.

## Examples

```bash
TOKEN="$(cat ~/.config/floxin/token)"
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:8080/api/v1/status
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:8080/api/v1/stats
curl -X POST -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \\
  -d '{"code":"SAVE-EG"}' http://127.0.0.1:8080/api/v1/scenario
```

The complete machine-readable specification is in [`openapi.yaml`](openapi.yaml). Swagger UI is available at `/docs` while the server is running.

## Safety

The server binds to loopback only. It does not accept a configurable public bind address. Rate limiting is applied per valid token. Configuration updates are written to a separate JSON file and do not rewrite `floxin_dns.py`.
