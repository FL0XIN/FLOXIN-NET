# NETTURBO

NETTURBO measures alternate HTTPS ports against one fixed comparison source, Cloudflare Speed (`speed.cloudflare.com/__down`), and recommends the fastest stable port. It uses three repeated downloads per port by default and stores measurements in SQLite. Keeping the CDN and payload constant makes port comparisons repeatable.

## Commands

```bash
NETTURBO scan
NETTURBO best
NETTURBO monitor
NETTURBO apply --port 8443
NETTURBO auto
NETTURBO stop
NETTURBO status
NETTURBO history --hours 24
NETTURBO report
```

The fixed default endpoint is `https://speed.cloudflare.com:<port>/__down?bytes=500000`, the payload is 500 KB, and the tested ports are `443, 8443, 2053, 2083, 2087, 2096, 8080, 8880`. For controlled diagnostics only, `FLOXIN_NETTURBO_HOST` and `FLOXIN_NETTURBO_BYTES` may override the source and payload; normal runs use the fixed default.

## Storage

```text
~/.floxin_netturbo/
├── config.json
├── history.db
├── log.txt
└── environment.sh
```

`NETTURBO apply` writes `HTTPS_PORT` and `FLOXIN_NETTURBO_PORT` to `environment.sh`; source it only in clients that support a configurable endpoint. `NETTURBO auto` starts a detached five-minute monitor and updates that client selection when a measured winner changes; `NETTURBO stop` ends the monitor. This setting does **not** transparently redirect every application or all system traffic. A system-wide redirect would require a proxy, VPN, or application-specific integration and is intentionally not enabled by this tool.

Measurements are recommendations, not a promise of higher bandwidth. Network policies, CDN behavior, TLS support, and the target application determine the actual result.
