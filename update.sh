#!/usr/bin/env bash
set -euo pipefail
REPO_URL="${FLOXIN_REPO_URL:-https://github.com/FL0XIN/FLOXIN-NET.git}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
command -v git >/dev/null 2>&1 || { echo 'git is required'; exit 1; }
git clone --depth 1 "$REPO_URL" "$TMP/repo"
bash "$TMP/repo/install.sh" "$@"
printf '%s\n' 'FLOXIN NET update complete.'
