# ~/.floxin_short.sh — Short codes for scenarios

# ═══ SHORT CODES ═══
# Format: SHORT|REAL|DESC
SHORT_CODES=(
    # ─── Numbers (fastest) ───
    "1|FAST|Fastest"
    "2|SAVE|Save data"
    "3|SAVE-WIFI|Save WiFi"
    "4|SAVE-SIM|Save SIM"
    "5|HIDE|Hide"
    "6|VPN|VPN"
    "7|ALL|Everything"
    "8|SMART|Auto"

    # ─── Single letters ───
    "F|FAST|Fastest"
    "S|SAVE|Save"
    "W|SAVE-WIFI|Save WiFi"
    "M|SAVE-SIM|Save SIM"
    "H|HIDE|Hide"
    "V|VPN|VPN"
    "A|ALL|All"
    "Z|SMART|Auto"

    # ─── Egypt presets ───
    "EG|BOOST-EG|Egypt fast"
    "EGS|SAVE-EG|Egypt + save"
    "EGV|SAVE-EG-VF|Vodafone + save"
    "EGE|SAVE-EG|Etisalat + save"

    # ─── Quick boost ───
    "B|BOOST-EG|Boost"
    "BC|BOOST-CF|Cloudflare"
    "BM|BOOST-MAX|Max boost"

    # ─── Quick save ───
    "SM|SAVE-MAX|Max save"
    "SA|SAVE-ADG|AdGuard save"

    # ─── Situations ───
    "AO|ALMOST-OUT|Almost out"
    "RO|RUNNING-OUT|Running out"
    "BL|BLOCKED|Blocked"
    "TH|THROTTLED|Throttled"

    # ─── Utility ───
    "C|QUICKCHECK|Check"
    "R|RESET|Reset"
    "X|STOP|Stop"
    "T|STATUS|Status"
    "L|LOG|History"
)

resolve_short() {
    local code=$(echo "$1" | tr '[:lower:]' '[:upper:]')
    for entry in "${SHORT_CODES[@]}"; do
        IFS='|' read -r s_id s_real s_desc <<< "$entry"
        if [ "$s_id" = "$code" ]; then
            echo "$s_real"
            return 0
        fi
    done
    echo ""
    return 1
}

list_short() {
    echo ""
    echo -e "  ${CC}═══ Short Codes ═══${CZ}"
    echo ""
    printf "  ${CG}%-6s${CZ} %-18s %s\n" "CODE" "RESULT" "DESC"
    printf "  %-6s %-18s %s\n" "──────" "──────────────────" "──────────────────"
    echo -e "  ${CY}── Numbers ──${CZ}"
    for entry in "${SHORT_CODES[@]}"; do
        IFS='|' read -r s_id s_real s_desc <<< "$entry"
        case "$s_id" in
            [0-9]) printf "  ${CG}%-6s${CZ} %-18s %s\n" "$s_id" "$s_real" "$s_desc" ;;
        esac
    done
    echo -e "  ${CY}── Letters ──${CZ}"
    for entry in "${SHORT_CODES[@]}"; do
        IFS='|' read -r s_id s_real s_desc <<< "$entry"
        case "$s_id" in
            [A-Z]) printf "  ${CG}%-6s${CZ} %-18s %s\n" "$s_id" "$s_real" "$s_desc" ;;
        esac
    done
    echo -e "  ${CY}── 2-3 Letters ──${CZ}"
    for entry in "${SHORT_CODES[@]}"; do
        IFS='|' read -r s_id s_real s_desc <<< "$entry"
        case "$s_id" in
            [A-Z][A-Z]|[A-Z][A-Z][A-Z]) printf "  ${CG}%-6s${CZ} %-18s %s\n" "$s_id" "$s_real" "$s_desc" ;;
        esac
    done
    echo ""
}
