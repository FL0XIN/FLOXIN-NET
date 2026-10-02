# FLOXIN NET Script Inventory

This inventory was verified against the current `main` tree, `bin/FLOXIN` dispatch, `install.sh`, `uninstall.sh`, Android Developer Console commands, and smoke tests.

## Executables retained

| File | Role | Used by |
|---|---|---|
| `bin/FLOXIN` | Primary interactive shell, aliases, configuration, diagnostics, API dispatch | User entry point |
| `bin/FLOXIN-API` | Start/stop/status/log for the local FastAPI service | `FLOXIN api`, API docs |
| `bin/NETGUARD` | Sleep protection with isolated strict DNS blocklist, timer, query log, and report | User entry point |
| `bin/DNSMGR` | DNS resolver lifecycle and DNS/VPN mode commands | User, `FLOXIN`, `NETMON`, tests |
| `bin/QUICKCHECK` | Fast connection, resolver, blocklist, and speed check | `FLOXIN test` fallback |
| `bin/DNSPICK` | DNS provider latency testing and provider selection | `FLOXIN` menu, `PRESAFE` |
| `bin/NETPROBE` | Network throttling diagnostic experiments (renamed from `WEHACK`; scanner expansion is a later phase) | `FLOXIN`, `PRESAFE` |
| `bin/PRESAFE` | Offline/throttled-network preparation and recommendations | `FLOXIN` |
| `bin/NETTHROTTLE` | Local proxy throttle simulator | `FLOXIN` |
| `bin/NETCHECK` | Full smoke and environment check | `FLOXIN test`, CI/manual tests |
| `bin/NETMON` | Live DNS query activity monitor | `FLOXIN` |
| `bin/NETSTAT` | Network, DNS, proxy, VPN, and speed status | `FLOXIN` |
| `bin/NETKILL` | Emergency stop/reset of local DNS and throttle processes | `FLOXIN` |

## Libraries retained

| File | Role | Evidence |
|---|---|---|
| `lib/.floxin_net.sh` | Shared runtime paths, lifecycle helpers, DNSPICK/NETPROBE integration | sourced by `FLOXIN`, `install.sh` |
| `lib/.floxin_codes.sh` | Canonical activation codes and provider catalog | sourced by `FLOXIN`, Android assets derived from it |
| `lib/.floxin_scenarios.sh` | Canonical scenarios | sourced by `FLOXIN`, Android assets derived from it |
| `lib/.floxin_simple.sh` | Beginner aliases and quick commands | sourced by `FLOXIN` and referenced by `FLOXIN simple` |
| `lib/.floxin_short.sh` | Short command aliases | sourced by `FLOXIN` and referenced by `FLOXIN short` |
| `lib/.floxin_sleepblock.sh` | NETGUARD isolated blocklist, state, and report helpers | sourced by `NETGUARD` |

## Cleanup result

The suspected legacy files `FLOXIN_NET`, `NETCTL`, `MASTER`, `B`, `dv`, and `EMERGENCY` are not present in the current repository. `EMERGENCY` is not a second implementation of `NETKILL`; neither file exists under that name in the current tree.

No current executable or library was safely removable: every retained item has a dispatch, installer, uninstaller, test, or source reference. Deleting any of them would break a documented command or a current integration. The only confirmed repository-only artifact was the empty, environment-specific `android/local.properties`, which was removed and added to `.gitignore`.

## Compatibility names

The former names remain as symlinks: `FLOXIN_API`, `DNSF`, `DNSWITCH`, `WEHACK`, `THROTTLE_SIM`, `SOUSG`, `NETSTATE`, `TESTALL`, `SAFE_MODE`, `PANIC`, and `CHECK`. New code and documentation use `FLOXIN-API`, `DNSMGR`, `DNSPICK`, `NETPROBE`, `NETTHROTTLE`, `NETMON`, `NETSTAT`, `NETCHECK`, `PRESAFE`, `NETKILL`, and `QUICKCHECK`.
