# ~/.floxin_simple.sh — Beginner-friendly aliases

# ═══ SIMPLE ALIASES ═══
# Format: ALIAS|REAL_SCENARIO|DESC
SIMPLE_ALIASES=(
    # ─── Results-oriented ───
    "FAST|BOOST-EG|Internet slow? Fastest mode"
    "SAVE|SAVE-ADG|Save data (auto network)"
    "SAVE-WIFI|SAVE-WIFI|Save data on WiFi"
    "SAVE-SIM|SAVE-SIM|Save data on SIM"
    "SAVE-MAX|SAVE-MAX|Max save (strict block)"
    "HIDE|HIDE-MULLVAD|Hide from ISP (privacy)"
    "ESCAPE|VPN-AUTO|Escape blocking via VPN"
    "VPN|VPN-WARP|Cloudflare WARP VPN"
    "ALL|ALL-MAX|Everything (DNS + VPN + block)"
    "SMART|SMART|Auto everything"

    # ─── Special situations ───
    "ALMOST-OUT|SAVE-MAX|Network almost out — save max"
    "RUNNING-OUT|SAVE-SIM|Network running out on SIM"
    "BLOCKED|ESCAPE|ISP blocking you — escape"
    "THROTTLED|VPN-WARP|Throttled — bypass with VPN"

    # ─── Utility ───
    "QUICKCHECK|QUICKCHECK|Check connection + status"
    "RESET|RESET|Reset to defaults"
    "STOP|STOP|Stop all"
    "STATUS|STATUS|Show status"
    "LOG|LOG|Show recent history"
)

# ═══ Resolve alias ═══
resolve_alias() {
    local alias=$(echo "$1" | tr '[:lower:]' '[:upper:]')
    for entry in "${SIMPLE_ALIASES[@]}"; do
        IFS='|' read -r a_id a_real a_desc <<< "$entry"
        if [ "$a_id" = "$alias" ]; then
            echo "$a_real"
            return 0
        fi
    done
    echo ""
    return 1
}

# ═══ List simple ═══
list_simple() {
    echo ""
    echo -e "  ${CG}═══════════ QUICK COMMANDS ═══════════${CZ}"
    echo -e "  ${CC}Just type these words — that's it${CZ}"
    echo ""
    printf "  ${CG}%-14s${CZ} %s\n" "TYPE" "RESULT"
    printf "  %-14s %s\n" "──────────────" "─────────────────────────────────"

    # Group 1: Results
    echo -e "  ${CY}─── I want... ───${CZ}"
    printf "  ${CG}%-14s${CZ} %s\n" "FAST" "Fastest internet"
    printf "  ${CG}%-14s${CZ} %s\n" "SAVE" "Save data (ad block)"
    printf "  ${CG}%-14s${CZ} %s\n" "SAVE-WIFI" "Save data on WiFi"
    printf "  ${CG}%-14s${CZ} %s\n" "SAVE-SIM" "Save data on SIM"
    printf "  ${CG}%-14s${CZ} %s\n" "SAVE-MAX" "Max save (strict)"
    printf "  ${CG}%-14s${CZ} %s\n" "HIDE" "Hide from ISP"
    printf "  ${CG}%-14s${CZ} %s\n" "VPN" "Use WARP VPN"
    printf "  ${CG}%-14s${CZ} %s\n" "ALL" "Everything at once"
    echo ""

    # Group 2: Situations
    echo -e "  ${CY}─── My situation... ───${CZ}"
    printf "  ${CG}%-14s${CZ} %s\n" "ALMOST-OUT" "Network almost out"
    printf "  ${CG}%-14s${CZ} %s\n" "BLOCKED" "ISP blocking me"
    printf "  ${CG}%-14s${CZ} %s\n" "THROTTLED" "Speed throttled"
    echo ""

    # Group 3: Utility
    echo -e "  ${CY}─── Utility ───${CZ}"
    printf "  ${CG}%-14s${CZ} %s\n" "QUICKCHECK" "Check status"
    printf "  ${CG}%-14s${CZ} %s\n" "LOG" "Show history"
    printf "  ${CG}%-14s${CZ} %s\n" "RESET" "Reset everything"
    printf "  ${CG}%-14s${CZ} %s\n" "STOP" "Stop all"
    echo ""
    echo -e "  ${CY}Chain: FAST SAVE VPN  (multiple at once)${CZ}"
    echo ""
}
