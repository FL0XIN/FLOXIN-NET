#!/usr/bin/env bash
set -euo pipefail
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
for f in "$ROOT"/bin/* "$ROOT"/install.sh "$ROOT"/setup.sh "$ROOT"/uninstall.sh "$ROOT"/update.sh; do
  bash -n "$f"
done
printf '%s\n' 'command syntax: ok'
