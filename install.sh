#!/usr/bin/env bash
# FLOXIN NET — portable installer (Android, Linux, macOS, WSL, Docker, proot)
set -u
ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/.floxin_env.sh
. "$ROOT_DIR/lib/.floxin_net.sh"

CG='\033[32m'; CR='\033[31m'; CY='\033[33m'; CC='\033[36m'; CZ='\033[0m'
INSTALL_FAIL=0
NO_INSTALL=0
[ "${1:-}" = "--no-install" ] && NO_INSTALL=1
ok(){ printf '  %b[OK]%b   %s\n' "$CG" "$CZ" "$1"; }
bad(){ printf '  %b[FAIL]%b %s\n' "$CR" "$CZ" "$1"; INSTALL_FAIL=$((INSTALL_FAIL+1)); }
warn(){ printf '  %b[WARN]%b %s\n' "$CY" "$CZ" "$1"; }
info(){ printf '  %b[INFO]%b %s\n' "$CC" "$CZ" "$1"; }

OS="$(floxin_os_name)"; PM="$(floxin_package_manager)"
HOME_BIN="${HOME}/bin"; DNS_DIR="${HOME}/dnsmasq"
mkdir -p "$HOME_BIN" "$DNS_DIR" || { bad "cannot create $HOME_BIN or $DNS_DIR"; exit 1; }

echo -e "${CC}=========================================${CZ}"
echo -e "${CC}  FLOXIN NET — Installer v1.1${CZ}"
echo -e "${CC}=========================================${CZ}"
info "Environment: $OS | package manager: $PM"
info "Architecture: $(uname -m 2>/dev/null || echo unknown) | kernel: $(uname -r 2>/dev/null || echo unknown)"
info "Root: $(floxin_has_root && echo yes || echo no) | sudo: $(floxin_has_sudo && echo yes || echo no) | proot: $(floxin_has_proot && echo yes || echo no)"

install_packages(){
    [ "$NO_INSTALL" -eq 1 ] && { info "Skipping package installation (--no-install)"; return 0; }
    local cmd=()
    case "$PM" in
      pkg) cmd=(pkg install -y python curl dnsutils net-tools) ;;
      apt|apt-get) cmd=(${PM} update -y); "${cmd[@]}" >/dev/null 2>&1 || true; cmd=(${PM} install -y python3 python3-pip curl dnsutils net-tools); ;;
      pacman) cmd=(pacman -Sy --noconfirm python python-pip curl bind net-tools); ;;
      dnf) cmd=(dnf install -y python3 python3-pip curl bind-utils net-tools); ;;
      apk) cmd=(apk add python3 py3-pip curl bind-tools net-tools); ;;
      brew) cmd=(brew install python curl bind net-tools); ;;
      none) warn "No supported package manager found; install dependencies manually"; return 0 ;;
      *) warn "Unsupported package manager: $PM"; return 0 ;;
    esac
    if "${cmd[@]}" >/dev/null 2>&1; then ok "system packages"; else bad "system packages (run manually or retry)"; fi
}

install_dnslib(){
    local py; py="$(floxin_python)"; [ -z "$py" ] && { bad "Python 3 is missing"; return; }
    "$py" -c 'import dnslib' >/dev/null 2>&1 && { ok "dnslib"; return; }
    local attempts=("$py -m pip install --user dnslib" "pip3 install --user dnslib" "pip install --user dnslib" "pipx install dnslib")
    local a; for a in "${attempts[@]}"; do
        if eval "$a" >/dev/null 2>&1 && "$py" -c 'import dnslib' >/dev/null 2>&1; then ok "dnslib installed"; return; fi
    done
    bad "dnslib installation failed"
}

install_packages
install_dnslib

# Deploy the existing project files; do not rebuild or discard user data.
for f in "$ROOT_DIR"/bin/*; do cp "$f" "$HOME_BIN/" && chmod +x "$HOME_BIN/$(basename "$f")" || bad "copy $(basename "$f")"; done
for f in "$ROOT_DIR"/lib/.*.sh; do cp "$f" "$HOME/" || bad "copy $(basename "$f")"; done
cp "$ROOT_DIR/dns/floxin_dns.py" "$DNS_DIR/" || bad "copy floxin_dns.py"
chmod +x "$DNS_DIR/floxin_dns.py"

BL="$DNS_DIR/blocklist.txt"
if [ -s "$BL" ] && [ "$(wc -l < "$BL")" -gt 10000 ]; then ok "existing blocklist ($(wc -l < "$BL") lines)"; else
    info "Downloading StevenBlack blocklist"
    tmp="${BL}.tmp"
    if command -v curl >/dev/null 2>&1 && curl -fsSL --max-time 45 -o "$tmp" "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts" && [ "$(wc -l < "$tmp")" -gt 10000 ]; then mv "$tmp" "$BL"; ok "blocklist downloaded"; else rm -f "$tmp"; warn "blocklist unavailable; DNS will run without blocking"; fi
fi

# Persist safe runtime defaults. Port 5353 avoids root requirements; users may override with FLOXIN_PORT.
cat > "$HOME/.floxin_environment" <<EOF
# FLOXIN NET runtime settings (shell-compatible)
export FLOXIN_PORT="${FLOXIN_PORT:-5353}"
export FLOXIN_BIND_ADDRESS="${FLOXIN_BIND_ADDRESS:-127.0.0.1}"
EOF
ok "runtime settings ($HOME/.floxin_environment)"

# Preserve Android auto-start convenience, but never modify shell startup files elsewhere.
if floxin_is_android_env && [ -f "$HOME/.bashrc" ] && ! grep -q 'FLOXIN Auto-start' "$HOME/.bashrc" 2>/dev/null; then
    cat >> "$HOME/.bashrc" <<'EOF'

# FLOXIN Auto-start (Android only)
[ -f "$HOME/.floxin_environment" ] && . "$HOME/.floxin_environment"
if [ -f "$HOME/dnsmasq/floxin_dns.py" ] && ! pgrep -f '[f]loxin_dns.py' >/dev/null 2>&1; then
    (cd "$HOME/dnsmasq" && nohup python3 floxin_dns.py > dns.log 2>&1 &)
fi
EOF
    ok "Android auto-start added"
else
    info "Auto-start not added; use FLOXIN bg or DNSF start"
fi

if [ "$INSTALL_FAIL" -eq 0 ]; then
    echo -e "${CG}Installation complete.${CZ} Run: $HOME_BIN/FLOXIN"
else
    echo -e "${CY}Installation completed with $INSTALL_FAIL issue(s).${CZ}"
fi
exit "$INSTALL_FAIL"
