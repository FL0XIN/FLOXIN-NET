# ~/.floxin_net.sh — FLOXIN Network Shared Library
# Read by DNSMGR and FLOXIN

# ═══ Paths ═══
export FLOXIN_DNS_DIR="$HOME/dnsmasq"
export FLOXIN_DNS_SCRIPT="$FLOXIN_DNS_DIR/floxin_dns.py"
export FLOXIN_DNS_LOG="$FLOXIN_DNS_DIR/dns.log"
export FLOXIN_DNS_PID="$FLOXIN_DNS_DIR/.dns.pid"
export FLOXIN_DNS_PORT=5353
export FLOXIN_LOCK="$FLOXIN_DNS_DIR/.dns.lock"

# ═══ Colors ═══
export FL_G="\033[32m"; export FL_R="\033[31m"
export FL_Y="\033[33m"; export FL_C="\033[36m"
export FL_B="\033[34m"; export FL_N="\033[0m"

# ═══ Checks ═══
floxin_running() {
    pgrep -f "floxin_dns.py" > /dev/null 2>&1
}

floxin_pid() {
    pgrep -f "floxin_dns.py" | head -1
}

floxin_query() {
    timeout 2 dig @127.0.0.1 -p $FLOXIN_DNS_PORT "$1" +short +timeout=1 2>/dev/null | grep -v ";" | head -1
}

floxin_ok() {
    local r=$(floxin_query "google.com")
    [ -n "$r" ] && [ "$r" != "0.0.0.0" ]
}

floxin_blocking() {
    local b=$(floxin_query "doubleclick.net")
    [ "$b" = "0.0.0.0" ]
}

# ═══ Start and stop ═══
floxin_start_bg() {
    if floxin_running; then
        return 0
    fi
    # Lock to prevent concurrent runs
    if [ -f "$FLOXIN_LOCK" ]; then
        local lock_age=$(( $(date +%s) - $(stat -c %Y "$FLOXIN_LOCK" 2>/dev/null || echo 0) ))
        if [ $lock_age -lt 30 ]; then
            echo "  ⚠️ Start operation in progress (since ${lock_age}s) — wait"
            return 1
        fi
    fi
    touch "$FLOXIN_LOCK"
    cd "$FLOXIN_DNS_DIR" || { rm -f "$FLOXIN_LOCK"; return 1; }
    nohup python3 "$FLOXIN_DNS_SCRIPT" > "$FLOXIN_DNS_LOG" 2>&1 &
    echo $! > "$FLOXIN_DNS_PID"
    disown
    sleep 3
    rm -f "$FLOXIN_LOCK"
    floxin_running
}

floxin_stop() {
    pkill -9 -f "floxin_dns.py" 2>/dev/null
    rm -f "$FLOXIN_DNS_PID" "$FLOXIN_LOCK"
    sleep 1
}

# ═══ FLOXIN_NET integration ═══
floxin_net() {
    if [ -x "$HOME/bin/FLOXIN_NET" ]; then
        bash "$HOME/bin/FLOXIN_NET" "$@"
    else
        echo "FLOXIN_NET not found"
    fi
}

# ═══ DNSPICK integration ═══
floxin_switch() {
    if [ -x "$HOME/bin/DNSPICK" ]; then
        bash "$HOME/bin/DNSPICK" "$@"
    else
        echo "DNSPICK not found"
    fi
}

# ═══ NETPROBE integration ═══
floxin_wehack() {
    if [ -x "$HOME/bin/NETPROBE" ]; then
        bash "$HOME/bin/NETPROBE" "$@"
    else
        echo "NETPROBE not found"
    fi
}

# ═══ FLOXIN_NET auto-import ═══
export -f floxin_running floxin_pid floxin_query floxin_ok floxin_blocking
export -f floxin_start_bg floxin_stop floxin_net floxin_switch floxin_wehack
#!/usr/bin/env bash
# FLOXIN NET environment detection helpers.
# Sourced by the installer and the interactive shell.

floxin_is_android_env() { [ -d /data/data/com.termux ] || [[ "${PREFIX:-}" == /data/data/com.termux/* ]]; }
floxin_is_wsl() { grep -qiE 'microsoft|wsl' /proc/version 2>/dev/null; }
floxin_is_docker() { [ -f /.dockerenv ] || grep -qaE 'docker|containerd' /proc/1/cgroup 2>/dev/null; }
floxin_is_proot() { command -v proot >/dev/null 2>&1 || [ -n "${PROOT_DISTRO:-}" ] || grep -qi proot /proc/$$/cmdline 2>/dev/null; }

floxin_os_name() {
    if floxin_is_android_env; then echo "Android"; return; fi
    if [ "$(uname -s 2>/dev/null)" = "Darwin" ]; then echo "macOS"; return; fi
    if floxin_is_wsl; then echo "WSL"; return; fi
    if floxin_is_docker; then echo "Docker"; return; fi
    if floxin_is_proot; then echo "proot-distro"; return; fi
    if [ -r /etc/os-release ]; then . /etc/os-release; echo "${PRETTY_NAME:-${ID:-Linux}}"; return; fi
    echo "$(uname -s 2>/dev/null || echo unknown)"
}

floxin_package_manager() {
    if floxin_is_android_env && command -v pkg >/dev/null 2>&1; then echo pkg; return; fi
    for pm in apt-get apt pacman dnf apk brew; do
        command -v "$pm" >/dev/null 2>&1 && { echo "$pm"; return; }
    done
    echo none
}

floxin_python() { command -v python3 || command -v python || true; }
floxin_has_root() { [ "$(id -u 2>/dev/null || echo 1)" = 0 ]; }
floxin_has_sudo() { command -v sudo >/dev/null 2>&1; }
floxin_has_proot() { command -v proot >/dev/null 2>&1; }

floxin_memory_mb() {
    if [ -r /proc/meminfo ]; then awk '/MemAvailable:/ {printf "%d", $2/1024; exit}' /proc/meminfo; else echo unknown; fi
}
floxin_disk_free_mb() { df -Pm "${HOME:-.}" 2>/dev/null | awk 'NR==2 {print $4; exit}'; }

floxin_print_environment() {
    echo "Environment: $(floxin_os_name)"
    echo "Kernel:     $(uname -sr 2>/dev/null || echo unknown)"
    echo "Arch:       $(uname -m 2>/dev/null || echo unknown)"
    echo "Python:     $(floxin_python || echo missing)"
    echo "Packages:   $(floxin_package_manager)"
    echo "Root:       $(floxin_has_root && echo yes || echo no)"
    echo "sudo:       $(floxin_has_sudo && echo yes || echo no)"
    echo "proot:      $(floxin_has_proot && echo yes || echo no)"
    echo "RAM free:   $(floxin_memory_mb) MB"
    echo "Disk free:  $(floxin_disk_free_mb) MB"
}
