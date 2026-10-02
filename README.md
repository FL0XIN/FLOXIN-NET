# FLOXIN NET

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

`FLOXIN` launches the interactive shell. `FLOXIN bg` starts the DNS service, `FLOXIN doctor` diagnoses and repairs dependencies, `FLOXIN env` displays system information, `FLOXIN export` creates a backup, and `FLOXIN DV` opens emergency mode. `DNSF start|stop|restart|status|test` manages the resolver. `TESTALL` runs the smoke checks. Existing quick commands such as `FAST`, `SAVE`, `HIDE`, `VPN`, and `ALL`, as well as `run <CODE>` and `code <CODE>`, remain supported.

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
