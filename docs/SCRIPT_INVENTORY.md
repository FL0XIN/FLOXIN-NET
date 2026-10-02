# FLOXIN NET Script Inventory

This inventory was verified against the current `main` tree, `bin/FLOXIN` dispatch, `install.sh`, `uninstall.sh`, Android Developer Console commands, and smoke tests.

## Executables retained

| File | Role | Used by |
|---|---|---|
| `bin/FLOXIN` | Primary interactive shell, aliases, configuration, diagnostics, API dispatch | User entry point |
| `bin/FLOXIN_API` | Start/stop/status/log for the local FastAPI service | `FLOXIN api`, API docs |
| `bin/DNSF` | DNS resolver lifecycle and DNS/VPN mode commands | User, `FLOXIN`, `SOUSG`, tests |
| `bin/CHECK` | Fast connection, resolver, blocklist, and speed check | `FLOXIN test` fallback |
| `bin/DNSWITCH` | DNS provider latency testing and provider selection | `FLOXIN` menu, `SAFE_MODE` |
| `bin/WEHACK` | Network throttling diagnostic experiments | `FLOXIN`, `SAFE_MODE` |
| `bin/SAFE_MODE` | Offline/throttled-network preparation and recommendations | `FLOXIN` |
| `bin/THROTTLE_SIM` | Local proxy throttle simulator | `FLOXIN` |
| `bin/TESTALL` | Full smoke and environment check | `FLOXIN test`, CI/manual tests |
| `bin/SOUSG` | Live DNS query activity monitor | `FLOXIN` |
| `bin/NETSTATE` | Network, DNS, proxy, VPN, and speed status | `FLOXIN` |
| `bin/PANIC` | Emergency stop/reset of local DNS and throttle processes | `FLOXIN` |

## Libraries retained

| File | Role | Evidence |
|---|---|---|
| `lib/.floxin_net.sh` | Shared runtime paths, lifecycle helpers, DNSWITCH/WEHACK integration | sourced by `FLOXIN`, `install.sh` |
| `lib/.floxin_codes.sh` | Canonical activation codes and provider catalog | sourced by `FLOXIN`, Android assets derived from it |
| `lib/.floxin_scenarios.sh` | Canonical scenarios | sourced by `FLOXIN`, Android assets derived from it |
| `lib/.floxin_simple.sh` | Beginner aliases and quick commands | sourced by `FLOXIN` and referenced by `FLOXIN simple` |
| `lib/.floxin_short.sh` | Short command aliases | sourced by `FLOXIN` and referenced by `FLOXIN short` |

## Cleanup result

The suspected legacy files `FLOXIN_NET`, `NETCTL`, `MASTER`, `B`, `dv`, and `EMERGENCY` are not present in the current repository. `EMERGENCY` is not a second implementation of `PANIC`; neither file exists under that name in the current tree.

No current executable or library was safely removable: every retained item has a dispatch, installer, uninstaller, test, or source reference. Deleting any of them would break a documented command or a current integration. The only confirmed repository-only artifact was the empty, environment-specific `android/local.properties`, which was removed and added to `.gitignore`.
