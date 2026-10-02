#!/usr/bin/env bash
set -euo pipefail
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
grep -q 'floxin_os_name' "$ROOT/install.sh"
grep -q 'floxin_package_manager' "$ROOT/install.sh"
grep -q 'dnslib' "$ROOT/install.sh"
printf '%s\n' 'installer checks: ok'
