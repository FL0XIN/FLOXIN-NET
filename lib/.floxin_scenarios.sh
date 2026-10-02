# ~/.floxin_scenarios.sh — Scenario Codes
# SCHEMA: CODE|TYPE|PROVIDER|NET|VPN|OPTS|DESC
# TYPE: SAVE|BOOST|HIDE|BLOCK|VPN|ALL|SMART
# VPN:  NONE|WARP|PSIPHON|AUTO

WARP_PKG="com.cloudflare.onedotonedotonedotone"
PSIPHON_PRO_PKG="com.psiphon3.subscription"
PSIPHON_FREE_PKG="com.psiphon3"

SCENARIO_CODES=(
    # ═══ DATA SAVER (7) ═══
    "SAVE-EG|SAVE|Egypt-TEData|AUTO|NONE||Save + Egypt server"
    "SAVE-EG-VF|SAVE|Egypt-Vodafone|AUTO|NONE||Save + Vodafone"
    "SAVE-ADG|SAVE|AdGuard|AUTO|NONE||Save + AdGuard block"
    "SAVE-MAX|SAVE|AdGuard|AUTO|NONE|blocklist=strict,cache=big|Max save (strict)"
    "SAVE-WIFI|SAVE|AdGuard|WIFI|NONE||Save on WiFi"
    "SAVE-SIM|SAVE|Egypt-TEData|SIM|NONE||Save on SIM"
    "SAVE-FAMILY|SAVE|AdGuard-Family|AUTO|NONE||Family safe + save"

    # ═══ BOOSTER (7) ═══
    "BOOST-EG|BOOST|Egypt-TEData|AUTO|NONE||Fastest Egypt"
    "BOOST-EG-VF|BOOST|Egypt-Vodafone|AUTO|NONE||Vodafone fastest"
    "BOOST-EG-ET|BOOST|Egypt-Etisalat|AUTO|NONE||Etisalat fastest"
    "BOOST-EG-OR|BOOST|Egypt-Orange|AUTO|NONE||Orange fastest"
    "BOOST-CF|BOOST|Cloudflare|AUTO|NONE||Global fastest"
    "BOOST-MAX|BOOST|Egypt-TEData|AUTO|NONE|cache=big,ttl=high|Ultra speed"
    "BOOST-ME|BOOST|UAE-Etisalat|AUTO|NONE||Middle East fast"

    # ═══ HIDE / PRIVACY (7) ═══
    "HIDE-MULLVAD|HIDE|Mullvad|AUTO|NONE||No-log privacy"
    "HIDE-NJALLA|HIDE|Njalla|AUTO|NONE||Njalla private"
    "HIDE-DNS0|HIDE|DNS0-EU|AUTO|NONE||DNS0.eu privacy"
    "HIDE-LIBRE|HIDE|LibreDNS|AUTO|NONE||LibreDNS"
    "HIDE-SB|HIDE|DNS-SB|AUTO|NONE||DNS.SB"
    "HIDE-ESCAPE|HIDE|Mullvad|AUTO|NONE|cache=off|Escape ISP tracking"
    "HIDE-VPN|HIDE|Mullvad|AUTO|WARP||Privacy + WARP"

    # ═══ BLOCK ONLY (5) ═══
    "BLOCK-ADS|BLOCK|AdGuard|AUTO|NONE||Ads + trackers only"
    "BLOCK-STRICT|BLOCK|AdGuard|AUTO|NONE|blocklist=strict|Strict block"
    "BLOCK-MAL|BLOCK|Quad9-Sec|AUTO|NONE||Malware block"
    "BLOCK-ADULT|BLOCK|CleanBrowsing-Adult|AUTO|NONE||Adult content block"
    "BLOCK-ALL|BLOCK|AdGuard|AUTO|NONE|blocklist=strict,log=off|Block all"

    # ═══ VPN ONLY (6) ═══
    "VPN-WARP|VPN|NONE|AUTO|WARP||Cloudflare WARP (fast)"
    "VPN-PSIPHON|VPN|NONE|AUTO|PSIPHON||Psiphon (bypass)"
    "VPN-AUTO|VPN|NONE|AUTO|AUTO||Auto pick VPN"
    "VPN-WARP-WIFI|VPN|NONE|WIFI|WARP||WARP on WiFi"
    "VPN-WARP-SIM|VPN|NONE|SIM|WARP||WARP on SIM"
    "VPN-PSIPHON-SIM|VPN|NONE|SIM|PSIPHON||Psiphon on SIM"

    # ═══ ALL-IN-ONE (5) ═══
    "ALL-EG|ALL|Egypt-TEData|AUTO|WARP||Egypt + Block + WARP"
    "ALL-CF|ALL|Cloudflare|AUTO|WARP||Cloudflare + Block + WARP"
    "ALL-MAX|ALL|Egypt-TEData|AUTO|WARP|blocklist=strict,cache=big|Ultimate"
    "ALL-WIFI|ALL|Egypt-TEData|WIFI|WARP||Full on WiFi"
    "ALL-SIM|ALL|Egypt-TEData|SIM|WARP||Full on SIM"

    # ═══ SMART (3) ═══
    "SMART|SMART|AUTO|AUTO|AUTO||Auto everything"
    "SMART-SAVE|SMART|AUTO|AUTO|NONE|blocklist=strict|Smart save"
    "SMART-BOOST|SMART|AUTO|AUTO|NONE|cache=big|Smart boost"
)

# ═══ VPN Launchers ═══
launch_app() {
    local pkg="$1"
    local name="$2"
    if command -v monkey >/dev/null 2>&1; then
        monkey -p "$pkg" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 && \
            echo "  ✓ $name launched" && return 0
    fi
    if command -v am >/dev/null 2>&1; then
        am start -n "$pkg"/.MainActivity >/dev/null 2>&1 && \
            echo "  ✓ $name launched" && return 0
    fi
    echo "  ⚠ Could not launch $name — open manually"
    return 1
}

launch_warp()    { launch_app "$WARP_PKG" "WARP"; }
launch_psiphon() {
    launch_app "$PSIPHON_PRO_PKG" "Psiphon Pro" || launch_app "$PSIPHON_FREE_PKG" "Psiphon"
}

# ═══ Parse scenario ═══
redeem_scenario() {
    local code=$(echo "$1" | tr '[:lower:]' '[:upper:]' | tr -d ' ')
    for entry in "${SCENARIO_CODES[@]}"; do
        IFS='|' read -r c_id c_type c_prov c_net c_vpn c_opts c_desc <<< "$entry"
        if [ "$c_id" = "$code" ]; then
            echo "TYPE=$c_type"
            echo "PROVIDER=$c_prov"
            echo "NET=$c_net"
            echo "VPN=$c_vpn"
            echo "OPTS=$c_opts"
            echo "DESC=$c_desc"
            return 0
        fi
    done
    return 1
}

# ═══ List scenarios ═══
list_scenarios() {
    echo ""
    echo -e "  ${CC}═══ Scenario Codes ═══${CZ}"
    echo ""
    printf "  %-18s %-6s %-18s %-8s %-8s %s\n" "CODE" "TYPE" "PROVIDER" "NET" "VPN" "DESC"
    printf "  %-18s %-6s %-18s %-8s %-8s %s\n" "──────────────────" "──────" "──────────────────" "────────" "────────" "──────────────────"
    local cur=""
    for entry in "${SCENARIO_CODES[@]}"; do
        IFS='|' read -r c_id c_type c_prov c_net c_vpn c_opts c_desc <<< "$entry"
        if [ "$c_type" != "$cur" ]; then
            echo -e "  ${CY}── $c_type ──${CZ}"
            cur="$c_type"
        fi
        printf "  ${CG}%-18s${CZ} %-6s %-18s %-8s %-8s %s\n" "$c_id" "$c_type" "$c_prov" "$c_net" "$c_vpn" "$c_desc"
    done
    echo ""
    echo -e "  ${CY}Use: run <CODE>${CZ}"
    echo ""
}
