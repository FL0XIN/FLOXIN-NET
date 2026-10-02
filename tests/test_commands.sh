#!/usr/bin/env bash
set -euo pipefail
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
for f in "$ROOT"/bin/* "$ROOT"/install.sh "$ROOT"/setup.sh "$ROOT"/uninstall.sh "$ROOT"/update.sh; do
  bash -n "$f"
done
printf '%s\n' 'command syntax: ok'

expected_bin=(FLOXIN FLOXIN_API DNSF CHECK DNSWITCH WEHACK SAFE_MODE THROTTLE_SIM TESTALL SOUSG NETSTATE PANIC)
expected_lib=(.floxin_net.sh .floxin_codes.sh .floxin_scenarios.sh .floxin_simple.sh .floxin_short.sh)
for name in "${expected_bin[@]}"; do test -x "$ROOT/bin/$name"; done
for name in "${expected_lib[@]}"; do test -f "$ROOT/lib/$name"; done
for name in FLOXIN_NET NETCTL MASTER B dv EMERGENCY; do test ! -e "$ROOT/bin/$name"; done
test ! -e "$ROOT/android/local.properties"
printf '%s\n' 'script inventory: ok'
