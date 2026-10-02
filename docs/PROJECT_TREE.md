# FLOXIN NET Project Tree

```text
FLOXIN-NET/
├── bin/                 # User-facing CLI entry points
│   ├── FLOXIN           # Main interactive shell
│   ├── NETGUARD         # Sleep protection mode
│   ├── NETSCAN          # Network quality analysis
│   ├── NETOPT           # Measurement-based optimization advice
│   ├── NETTURBO         # Alternate CDN port measurements
│   ├── DNSMGR           # DNS lifecycle manager
│   ├── DNSPICK          # DNS provider selection
│   ├── NETMON           # DNS activity monitor
│   ├── NETSTAT          # Network status
│   ├── NETCHECK         # Full checks
│   └── legacy symlinks  # Backward-compatible command names
├── lib/                 # Small shell integrations and catalogs
├── tools/               # Python measurement engines
│   ├── netscan.py
│   ├── netopt.py
│   └── netturbo.py
├── dns/                 # Local DNS resolver
├── api/                 # Optional localhost REST API
├── android/             # Android client
├── docs/                # Architecture, API, and feature documentation
├── tests/               # Shell and API tests
├── install.sh           # Installer
├── update.sh            # Updater
└── uninstall.sh         # Safe program-file removal
```

Source files are intentionally kept readable and versioned. Sensitive runtime data such as tokens, logs, history databases, local SDK paths, and user configuration belongs outside the repository and is covered by `.gitignore` where applicable.
