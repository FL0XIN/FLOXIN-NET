#!/usr/bin/env bash
set -euo pipefail
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
python3 -m py_compile "$ROOT/dns/floxin_dns.py"
python3 - <<PY
import ast
ast.parse(open("$ROOT/dns/floxin_dns.py", encoding="utf-8").read())
print("dns syntax: ok")
PY
