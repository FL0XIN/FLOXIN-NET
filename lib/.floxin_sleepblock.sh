#!/usr/bin/env bash
# FLOXIN NETGUARD shared helpers. DNS-level fallback is always safe; iptables is not changed automatically.
NETGUARD_DIR="${FLOXIN_NETGUARD_DIR:-$HOME/.floxin_netguard}"
NETGUARD_BLOCKLIST="$NETGUARD_DIR/blocklist.txt"
NETGUARD_LOG="$NETGUARD_DIR/queries.log"
NETGUARD_PID="$NETGUARD_DIR/guard.pid"
NETGUARD_META="$NETGUARD_DIR/state"

netguard_sync_domains() {
    cat <<'LIST'
accounts.google.com
android.clients.google.com
connectivitycheck.gstatic.com
firebaseinstallations.googleapis.com
fcm.googleapis.com
www.googleapis.com
graph.facebook.com
b-graph.facebook.com
api.facebook.com
connect.facebook.net
edge-mqtt.facebook.com
updates.facebook.com
api.instagram.com
gateway.instagram.com
connectivitycheck.android.com
play.googleapis.com
play-fe.googleapis.com
clients3.google.com
clients4.google.com
push.services.mozilla.com
updates.push.services.mozilla.com
LIST
}

netguard_prepare() {
    mkdir -p "$NETGUARD_DIR"
    : > "$NETGUARD_BLOCKLIST"
    if [ -f "${FLOXIN_DNS_DIR:-$HOME/dnsmasq}/blocklist.txt" ]; then
        cat "${FLOXIN_DNS_DIR:-$HOME/dnsmasq}/blocklist.txt" >> "$NETGUARD_BLOCKLIST"
    fi
    while IFS= read -r domain; do
        [ -z "$domain" ] && continue
        printf '0.0.0.0 %s\n' "$domain" >> "$NETGUARD_BLOCKLIST"
    done < <(netguard_sync_domains)
    printf 'started_at=%s\n' "$(date +%s)" > "$NETGUARD_META"
}

netguard_report() {
    local attempts domains mb
    attempts=0; domains=0
    if [ -f "$NETGUARD_LOG" ]; then
        attempts=$(wc -l < "$NETGUARD_LOG" 2>/dev/null || echo 0)
        domains=$(awk -F'|' 'NF >= 4 { seen[$4]=1 } END { print length(seen)+0 }' "$NETGUARD_LOG" 2>/dev/null || echo 0)
    fi
    # Conservative estimate: blocked DNS attempt ~= 50 KiB avoided background traffic.
    mb=$(awk -v n="$attempts" 'BEGIN { printf "%.2f", (n*50/1024) }')
    printf 'NETGUARD report: %s attempts | %s domains | ~%s MB estimated\n' "$attempts" "$domains" "$mb"
}
