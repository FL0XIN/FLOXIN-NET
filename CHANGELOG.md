# Changelog

## 1.1.0 — 2026-10-02

- Added automatic environment detection for Termux, Linux distributions, macOS, WSL, Docker, and proot-distro.
- Added architecture, RAM, free-space, package-manager, root, sudo, and proot detection.
- Reworked the existing installer to deploy the current project files on supported systems.
- Added portable `setup.sh`, `uninstall.sh`, and `update.sh` wrappers.
- Made DNS bind address and port configurable; localhost:5353 is the safe default.
- Added repository documentation, smoke tests, and GitHub Actions CI.
- Preserved the existing FLOXIN commands and data directories.
