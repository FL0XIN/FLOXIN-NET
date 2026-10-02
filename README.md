# FLOXIN NET

[![Tests](https://github.com/FL0XIN/FLOXIN-NET/actions/workflows/test.yml/badge.svg)](https://github.com/FL0XIN/FLOXIN-NET/actions/workflows/test.yml) [![Android Debug APK](https://github.com/FL0XIN/FLOXIN-NET/actions/workflows/android-debug.yml/badge.svg)](https://github.com/FL0XIN/FLOXIN-NET/actions/workflows/android-debug.yml) [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Local DNS ad blocker, network diagnostics, and measurement-based optimization for Linux, macOS, Android terminals, WSL, Docker, and proot.**

Keywords: local DNS, ad blocking, DNS cache, network diagnostics, latency, jitter, packet loss, MTU, TCP tuning, CDN port measurement, Android VPN DNS, privacy tools.

FLOXIN NET is a cross-platform local DNS utility for Linux, macOS, Android terminals, WSL, Docker, and proot environments. The FLOXIN command shell is the primary interface, and the installer auto-detects the host environment.

## What it does

The project can run a local DNS resolver with caching and an optional StevenBlack hosts blocklist. It also keeps the existing data-saver, booster, provider switching, VPN-conflict checks, diagnostics, scenarios, activation codes, export, and emergency commands.

> FLOXIN NET does not create a VPN, guarantee higher bandwidth, or bypass provider policies. DNS performance and filtering depend on the selected upstream resolver and the local network.

## Supported environments

The installer detects Android terminals, Ubuntu/Debian, Arch, Fedora, Alpine, macOS, WSL, Docker, and proot-distro. It reports the OS, kernel, architecture, available RAM, free disk, package manager, root, sudo, and proot availability.

The safe default is `127.0.0.1:5353`, which requires no root. Advanced users may set `FLOXIN_PORT` and `FLOXIN_BIND_ADDRESS`; exposing a DNS service beyond localhost should only be done on a trusted network and with appropriate firewall rules.

## Install the existing version

```bash
git clone https://github.com/FL0XIN/FLOXIN-NET.git
cd floxin-net
./setup.sh
```

Use `./install.sh --no-install` when system packages are already present and no package-manager changes should be made. The installer deploys the current `bin/`, `lib/`, and `dns/` files into `~/bin`, `~/`, and `~/dnsmasq` respectively. It downloads the blocklist only when it is missing or too small.

## Commands

`FLOXIN` launches the interactive shell. `FLOXIN start` starts the DNS service, `FLOXIN doctor` diagnoses and repairs dependencies, `FLOXIN env` displays system information, `FLOXIN export` creates a backup, and `FLOXIN rescue` opens emergency mode. Run `FLOXIN help` for the complete in-app catalog. `DNSMGR start|stop|restart|status|test` manages the resolver. `NETCHECK` runs the smoke checks. Existing quick commands such as `FAST`, `SAVE`, `HIDE`, `VPN`, and `ALL`, as well as `run <CODE>` and `code <CODE>`, remain supported.

`NETGUARD start --hours 8` enables sleep protection using an isolated strict DNS blocklist, records blocked query attempts under `~/.floxin_netguard/`, and stops automatically after the timer. Use `NETGUARD status`, `NETGUARD log`, or `NETGUARD stop` to inspect or end the session. NETGUARD does not change iptables automatically; DNS-level blocking is the safe fallback on unrooted systems.

Phase 1 analysis tools are `NETSCAN` (`network`, `quality`, `latency`, `jitter`, `packet-loss`, `dpi-probe`, `qos-detect`) and `NETOPT` (`auto`, `mtu-probe`, `mtu-set`, `tcp-tuning`, `port-test`, `route-test`, `dns-optimize`, `protocol`, `status`, `reset`). They measure first and do not silently alter system networking. See [docs/NETWORK_ANALYSIS.md](docs/NETWORK_ANALYSIS.md).

`NETTURBO scan` compares eight HTTPS ports with repeated CDN measurements and stores history in `~/.floxin_netturbo/`. Use `NETTURBO best`, `NETTURBO monitor`, `NETTURBO apply --port 8443`, `NETTURBO auto`, `NETTURBO stop`, `NETTURBO status`, `NETTURBO history`, and `NETTURBO report`. Applying a port writes a client environment proposal; it does not transparently reroute all system traffic. See [docs/NETTURBO.md](docs/NETTURBO.md).

### Complete new-tool command list

| Tool | Commands |
|---|---|
| `NETGUARD` | `start`, `stop`, `status`, `log`, `help` |
| `NETSCAN` | `network`, `quality`, `latency`, `jitter`, `packet-loss`, `dpi-probe`, `qos-detect` |
| `NETOPT` | `auto`, `mtu-probe`, `mtu-set`, `tcp-tuning`, `port-test`, `route-test`, `dns-optimize`, `protocol`, `status`, `reset` |
| `NETTURBO` | `scan`, `best`, `apply`, `monitor`, `auto`, `status`, `stop`, `history`, `report` |

All new tools are measurement-first, no-root utilities. They do not promise extra bandwidth, bypass provider policies, or silently modify system networking.

The maintained executable and library inventory is documented in [docs/SCRIPT_INVENTORY.md](docs/SCRIPT_INVENTORY.md). The current tree does not contain the previously suspected legacy names (`FLOXIN_NET`, `NETCTL`, `MASTER`, `B`, `dv`, or `EMERGENCY`). The remaining scripts are intentionally separate because they provide distinct diagnostics, lifecycle, monitoring, simulation, API, or recovery functions.

The previous command names remain available as symlinks for compatibility, including `DNSF`, `DNSWITCH`, `WEHACK`, `THROTTLE_SIM`, `SOUSG`, `NETSTATE`, `TESTALL`, `SAFE_MODE`, `PANIC`, `CHECK`, and `FLOXIN_API`.

## Languages

[English](README.md) · [العربية](README_AR.md) · [简体中文](README_ZH.md) · [Русский](README_RU.md)

The organized repository tree is documented in [docs/PROJECT_TREE.md](docs/PROJECT_TREE.md).

## Security and privacy notes

The resolver binds to localhost by default. Query logs are stored locally in `~/dnsmasq/queries.log`; they may contain domain names and should be treated as private. Upstream DNS requests go to the configured provider. Review providers and blocklists before use, and do not run downloaded shell code without inspecting it.

The uninstall script removes installed program files but preserves user data, including blocklists, logs, configuration, and backups.

## Development checks

```bash
bash tests/test_commands.sh
bash tests/test_dns.sh
bash tests/test_install.sh
```

## License

MIT. See [LICENSE](LICENSE).
