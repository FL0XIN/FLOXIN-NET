CODES_FILE="$HOME/dnsmasq/.floxin_codes_used"
GEO_CACHE="$HOME/dnsmasq/.floxin_geo"

EXTENDED_PROVIDERS=(
    "Egypt-TEData|62.240.33.4|62.240.33.5|EG"
    "Egypt-Vodafone|163.121.128.134|163.121.128.135|EG"
    "Egypt-Etisalat|62.135.32.2|62.135.32.3|EG"
    "Egypt-Orange|62.135.32.2|62.135.32.3|EG"
    "Egypt-Link|196.221.1.1|196.221.1.2|EG"
    "Egypt-Noor|196.32.128.1|196.32.128.2|EG"
    "UAE-Etisalat|94.200.200.200|94.200.200.201|ME"
    "UAE-DU|94.200.200.100|94.200.200.101|ME"
    "Saudi-STC|212.118.116.1|212.118.116.2|ME"
    "Saudi-Mobily|212.118.116.10|212.118.116.11|ME"
    "Kuwait-Zain|62.150.24.1|62.150.24.2|ME"
    "Qatar-Ooredoo|212.77.192.1|212.77.192.2|ME"
    "Bahrain-Batelco|195.175.12.1|195.175.12.2|ME"
    "Oman-Omantu|85.154.16.1|85.154.16.2|ME"
    "Jordan-Orange|79.173.128.1|79.173.128.2|ME"
    "Germany-DFN|194.95.1.1|194.95.1.2|EU"
    "France-OVH|213.186.33.99|213.186.33.100|EU"
    "UK-BT|194.72.0.1|194.72.0.2|EU"
    "Netherlands-XS4ALL|195.121.1.1|195.121.1.2|EU"
    "Sweden-Telia|81.228.1.1|81.228.1.2|EU"
    "Italy-Telecom|85.37.1.1|85.37.1.2|EU"
    "Spain-Telefonica|80.58.1.1|80.58.1.2|EU"
    "Poland-Orange|194.204.1.1|194.204.1.2|EU"
    "US-Verizon|4.2.2.1|4.2.2.2|US"
    "US-ATT|12.127.16.1|12.127.16.2|US"
    "US-Level3|4.2.2.1|4.2.2.2|US"
    "US-Google2|8.8.8.8|8.8.4.4|US"
    "Japan-IIJ|202.32.1.1|202.32.1.2|ASIA"
    "Korea-KT|168.126.63.1|168.126.63.2|ASIA"
    "Singapore-StarHub|203.116.1.1|203.116.1.2|ASIA"
    "HongKong-HGC|203.80.1.1|203.80.1.2|ASIA"
    "Taiwan-HiNet|168.95.1.1|168.95.1.2|ASIA"
    "India-Jio|49.44.1.1|49.44.1.2|ASIA"
    "Cloudflare|1.1.1.1|1.0.0.1|GLOBAL"
    "Cloudflare-Sec|1.1.1.2|1.0.0.2|GLOBAL"
    "Cloudflare-Family|1.1.1.3|1.0.0.3|GLOBAL"
    "Google|8.8.8.8|8.8.4.4|GLOBAL"
    "Quad9|9.9.9.9|149.112.112.112|GLOBAL"
    "Quad9-Sec|9.9.9.10|149.112.112.10|GLOBAL"
    "AdGuard|94.140.14.14|94.140.15.15|GLOBAL"
    "AdGuard-Family|94.140.14.15|94.140.15.16|GLOBAL"
    "AdGuard-NoFilter|94.140.14.140|94.140.14.141|GLOBAL"
    "OpenDNS|208.67.222.222|208.67.220.220|GLOBAL"
    "OpenDNS-Family|208.67.222.123|208.67.220.123|GLOBAL"
    "Mullvad|194.242.2.2|194.242.2.3|GLOBAL"
    "DNS-SB|185.222.222.222|45.11.45.11|GLOBAL"
    "CleanBrowsing|185.228.168.9|185.228.169.9|GLOBAL"
    "CleanBrowsing-Family|185.228.168.168|185.228.169.168|GLOBAL"
    "CleanBrowsing-Adult|185.228.168.10|185.228.169.11|GLOBAL"
    "NextDNS|45.90.28.0|45.90.30.0|GLOBAL"
    "Comodo|8.26.56.26|8.20.247.20|GLOBAL"
    "Yandex|77.88.8.8|77.88.8.1|GLOBAL"
    "Yandex-Family|77.88.8.7|77.88.8.3|GLOBAL"
    "DNS0-EU|193.110.81.0|185.253.5.0|EU"
    "Foundation|198.101.242.72|198.101.242.73|GLOBAL"
    "Njalla|95.215.19.53|95.215.19.54|GLOBAL"
    "LibreDNS|116.202.176.26|116.202.176.27|EU"
    "Cisco-Umbrella|208.67.222.222|208.67.220.220|GLOBAL"
    "Fortinet|208.91.112.52|208.91.112.53|GLOBAL"
    "Neustar|156.154.70.1|156.154.71.1|GLOBAL"
    "Hurricane|74.82.42.42|74.82.42.43|GLOBAL"
    "Verisign|64.6.64.6|64.6.65.6|GLOBAL"
    "SafeDNS|195.46.39.39|195.46.39.40|GLOBAL"
    "Alternate|193.19.108.2|193.19.108.3|EU"
    "DNSWatch|84.200.69.80|84.200.70.40|EU"
)

CODES=(
    "FLOX-EG|BOOSTER|Egypt-TEData|AUTO||Egypt local fastest"
    "FLOX-EG-TE|BOOSTER|Egypt-TEData|AUTO||TE Data fast"
    "FLOX-EG-VF|BOOSTER|Egypt-Vodafone|AUTO||Vodafone fast"
    "FLOX-EG-ET|BOOSTER|Egypt-Etisalat|AUTO||Etisalat fast"
    "FLOX-EG-OR|BOOSTER|Egypt-Orange|AUTO||Orange fast"
    "FLOX-EG-LINK|BOOSTER|Egypt-Link|AUTO||Link DSL"
    "FLOX-EG-NOOR|BOOSTER|Egypt-Noor|AUTO||Noor DSL"
    "FLOX-EG-SAVE|DATA_SAVER|Egypt-TEData|AUTO||Egypt + ad block"
    "FLOX-EG-2X1|2X1|Egypt-TEData|AUTO||Egypt dual"
    "FLOX-EG-MAX|BOOSTER|Egypt-TEData|AUTO|cache=big,ttl=high|Max speed"
    "FLOX-ME|BOOSTER|UAE-Etisalat|AUTO||Middle East fast"
    "FLOX-UAE|BOOSTER|UAE-Etisalat|AUTO||UAE Etisalat"
    "FLOX-UAE-DU|BOOSTER|UAE-DU|AUTO||UAE DU"
    "FLOX-SA|BOOSTER|Saudi-STC|AUTO||Saudi STC"
    "FLOX-SA-MOB|BOOSTER|Saudi-Mobily|AUTO||Saudi Mobily"
    "FLOX-KW|BOOSTER|Kuwait-Zain|AUTO||Kuwait Zain"
    "FLOX-QA|BOOSTER|Qatar-Ooredoo|AUTO||Qatar"
    "FLOX-ME-SAVE|DATA_SAVER|UAE-Etisalat|AUTO||ME + ad block"
    "FLOX-GLOBAL|BOOSTER|Cloudflare|AUTO||Cloudflare balanced"
    "FLOX-CF|BOOSTER|Cloudflare|AUTO||Cloudflare"
    "FLOX-CF-SEC|DATA_SAVER|Cloudflare-Sec|AUTO||Cloudflare Security"
    "FLOX-CF-FAM|DATA_SAVER|Cloudflare-Family|AUTO||Cloudflare Family"
    "FLOX-GG|BOOSTER|Google|AUTO||Google DNS"
    "FLOX-QUAD9|BOOSTER|Quad9|AUTO||Quad9"
    "FLOX-QUAD9-SEC|DATA_SAVER|Quad9-Sec|AUTO||Quad9 Security"
    "FLOX-ADG|DATA_SAVER|AdGuard|AUTO||AdGuard max block"
    "FLOX-ADG-FAM|DATA_SAVER|AdGuard-Family|AUTO||AdGuard Family"
    "FLOX-ADG-NF|BOOSTER|AdGuard-NoFilter|AUTO||AdGuard no filter"
    "FLOX-OPENDNS|DATA_SAVER|OpenDNS|AUTO||OpenDNS"
    "FLOX-OPENDNS-FAM|DATA_SAVER|OpenDNS-Family|AUTO||OpenDNS Family"
    "FLOX-CLEAN|DATA_SAVER|CleanBrowsing|AUTO||CleanBrowsing"
    "FLOX-CLEAN-FAM|DATA_SAVER|CleanBrowsing-Family|AUTO||CleanBrowsing Family"
    "FLOX-CLEAN-AD|DATA_SAVER|CleanBrowsing-Adult|AUTO||Block adult"
    "FLOX-ESCAPE|2X1|Mullvad|AUTO||Bypass ISP blocking"
    "FLOX-STEALTH|BOOSTER|Mullvad|AUTO||No-log privacy"
    "FLOX-NJALLA|BOOSTER|Njalla|AUTO||Njalla private"
    "FLOX-DNS-SB|BOOSTER|DNS-SB|AUTO||DNS.SB privacy"
    "FLOX-LIBRE|BOOSTER|LibreDNS|AUTO||LibreDNS"
    "FLOX-DNS0|BOOSTER|DNS0-EU|AUTO||DNS0.eu privacy"
    "FLOX-FOUND|BOOSTER|Foundation|AUTO||Foundation DNS"
    "FLOX-SAFE|DATA_SAVER|SafeDNS|AUTO||SafeDNS filter"
    "FLOX-SECURE|DATA_SAVER|Quad9-Sec|AUTO||Malware block"
    "FLOX-CISCO|DATA_SAVER|Cisco-Umbrella|AUTO||Cisco Umbrella"
    "FLOX-FORTI|DATA_SAVER|Fortinet|AUTO||Fortinet security"
    "FLOX-NEUSTAR|DATA_SAVER|Neustar|AUTO||Neustar security"
    "FLOX-HURR|BOOSTER|Hurricane|AUTO||Hurricane Electric"
    "FLOX-VERISIGN|DATA_SAVER|Verisign|AUTO||Verisign"
    "FLOX-DNSWATCH|DATA_SAVER|DNSWatch|AUTO||DNSWatch"
    "FLOX-ALTERNATE|BOOSTER|Alternate|AUTO||Alternate DNS"
    "FLOX-EU|BOOSTER|DNS0-EU|AUTO||EU nearby"
    "FLOX-DE|BOOSTER|Germany-DFN|AUTO||Germany DFN"
    "FLOX-FR|BOOSTER|France-OVH|AUTO||France OVH"
    "FLOX-UK|BOOSTER|UK-BT|AUTO||UK BT"
    "FLOX-NL|BOOSTER|Netherlands-XS4ALL|AUTO||Netherlands"
    "FLOX-SE|BOOSTER|Sweden-Telia|AUTO||Sweden Telia"
    "FLOX-IT|BOOSTER|Italy-Telecom|AUTO||Italy Telecom"
    "FLOX-ES|BOOSTER|Spain-Telefonica|AUTO||Spain Telefonica"
    "FLOX-US|BOOSTER|US-Verizon|AUTO||US Verizon"
    "FLOX-VZ|BOOSTER|US-Verizon|AUTO||Verizon"
    "FLOX-ATT|BOOSTER|US-ATT|AUTO||AT&T"
    "FLOX-L3|BOOSTER|US-Level3|AUTO||Level3"
    "FLOX-US-SAVE|DATA_SAVER|US-Level3|AUTO||US + ad block"
    "FLOX-ASIA|BOOSTER|Singapore-StarHub|AUTO||Asia nearby"
    "FLOX-JP|BOOSTER|Japan-IIJ|AUTO||Japan IIJ"
    "FLOX-KR|BOOSTER|Korea-KT|AUTO||Korea KT"
    "FLOX-SG|BOOSTER|Singapore-StarHub|AUTO||Singapore"
    "FLOX-HK|BOOSTER|HongKong-HGC|AUTO||Hong Kong"
    "FLOX-TW|BOOSTER|Taiwan-HiNet|AUTO||Taiwan HiNet"
    "FLOX-IN|BOOSTER|India-Jio|AUTO||India Jio"
    "FLOX-BOOST|BOOSTER|Cloudflare|AUTO||Pure speed"
    "FLOX-SAVE|DATA_SAVER|AdGuard|AUTO||Max data save"
    "FLOX-2X1|2X1|Cloudflare|AUTO||Dual mode"
    "FLOX-ULTRA-SAVE|DATA_SAVER|AdGuard|AUTO|blocklist=strict,cache=big|Ultra data saver"
    "FLOX-ULTRA-BOOST|BOOSTER|Cloudflare|AUTO|cache=big,ttl=high|Ultra speed"
    "FLOX-SILENT|BOOSTER|Cloudflare|AUTO|log=off|No logging"
    "FLOX-VERBOSE|DATA_SAVER|Cloudflare|AUTO|log=on|Detailed logging"
    "FLOX-NO-CACHE|BOOSTER|Google|AUTO|cache=off|No DNS cache"
    "FLOX-CACHE-BIG|BOOSTER|Cloudflare|AUTO|cache=big|Large cache"
    "FLOX-CACHE-OFF|BOOSTER|Cloudflare|AUTO|cache=off|No cache"
    "FLOX-TTL-HIGH|BOOSTER|Cloudflare|AUTO|ttl=high|Long TTL"
    "FLOX-TTL-LOW|BOOSTER|Cloudflare|AUTO|ttl=low|Short TTL"
    "FLOX-LOG-OFF|BOOSTER|Cloudflare|AUTO|log=off|Disable log"
    "FLOX-LOG-ON|DATA_SAVER|Cloudflare|AUTO|log=on|Enable log"
    "FLOX-STRICT|DATA_SAVER|AdGuard|AUTO|blocklist=strict|Strict blocklist"
    "FLOX-CHILL|DATA_SAVER|AdGuard|AUTO|blocklist=light|Light blocklist"
    "FLOX-LOW-LATENCY|BOOSTER|Egypt-TEData|AUTO|cache=big,ttl=low|Low latency"
    "FLOX-HIGH-THRU|BOOSTER|Cloudflare|AUTO|cache=big,ttl=high|High throughput"
    "FLOX-WIFI|BOOSTER|Cloudflare|WIFI||WiFi mode"
    "FLOX-SIM|BOOSTER|Egypt-TEData|SIM||SIM mode"
    "FLOX-BOTH|BOOSTER|Cloudflare|BOTH||WiFi + SIM"
    "FLOX-WIFI-SAVE|DATA_SAVER|AdGuard|WIFI||WiFi + save"
    "FLOX-SIM-SAVE|DATA_SAVER|Egypt-TEData|SIM||SIM + save"
    "FLOX-BOTH-SAVE|DATA_SAVER|AdGuard|BOTH||Both + save"
    "FLOX-DOCTOR|BOOSTER|Cloudflare|AUTO||Run doctor"
    "FLOX-PANIC|BOOSTER|Cloudflare|AUTO||Emergency reset"
    "FLOX-MONITOR|BOOSTER|Cloudflare|AUTO||Live monitor"
    "FLOX-TEST|BOOSTER|Cloudflare|AUTO||Full test"
    "FLOX-SWITCH|BOOSTER|Cloudflare|AUTO||Auto-switch"
    "FLOX-SCAN|BOOSTER|Cloudflare|AUTO||Port scan"
    "FLOX-STATUS|BOOSTER|Cloudflare|AUTO||Status only"
)

geo_detect() {
    if [ -f "$GEO_CACHE" ]; then
        local age=$(( $(date +%s) - $(stat -c %Y "$GEO_CACHE" 2>/dev/null || echo 0) ))
        if [ $age -lt 86400 ]; then
            source "$GEO_CACHE" 2>/dev/null
            [ -n "$GEO_COUNTRY" ] && return 0
        fi
    fi
    local resp=$(curl -s -m 6 "http://ip-api.com/json/?fields=status,country,countryCode,regionName,city,isp,query" 2>/dev/null)
    [ -z "$resp" ] && return 1
    local status=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('status',''))" 2>/dev/null)
    [ "$status" != "success" ] && return 1
    GEO_COUNTRY=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('country',''))" 2>/dev/null)
    GEO_CODE=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('countryCode',''))" 2>/dev/null)
    GEO_CITY=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('city',''))" 2>/dev/null)
    GEO_ISP=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('isp',''))" 2>/dev/null)
    GEO_IP=$(echo "$resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('query',''))" 2>/dev/null)
    cat > "$GEO_CACHE" << ENDGEO
GEO_COUNTRY="$GEO_COUNTRY"
GEO_CODE="$GEO_CODE"
GEO_CITY="$GEO_CITY"
GEO_ISP="$GEO_ISP"
GEO_IP="$GEO_IP"
ENDGEO
    return 0
}

auto_pick_provider() {
    local mode="$1"
    geo_detect
    if [ "$GEO_CODE" = "EG" ]; then
        case "$mode" in
            BOOSTER)
                case "$GEO_ISP" in
                    *Vodafone*) echo "Egypt-Vodafone" ;;
                    *Etisalat*) echo "Egypt-Etisalat" ;;
                    *Orange*)   echo "Egypt-Orange" ;;
                    *) echo "Egypt-TEData" ;;
                esac ;;
            DATA_SAVER) echo "AdGuard" ;;
            2X1) echo "Cloudflare" ;;
            *) echo "Egypt-TEData" ;;
        esac
        return
    fi
    case "$GEO_CODE" in
        SA|AE|KW|QA|BH|OM|JO|LB|IQ|SY|YE|PS)
            [ "$mode" = "DATA_SAVER" ] && echo "AdGuard" || echo "UAE-Etisalat" ;;
        *)
            case "$mode" in
                DATA_SAVER) echo "AdGuard" ;;
                *) echo "Cloudflare" ;;
            esac ;;
    esac
}

auto_pick_escape() { echo "Mullvad"; }

redeem_code() {
    local code="$1"
    code=$(echo "$code" | tr '[:lower:]' '[:upper:]' | tr -d ' ')
    for entry in "${CODES[@]}"; do
        IFS='|' read -r c_id c_mode c_prov c_net c_opts c_desc <<< "$entry"
        if [ "$c_id" = "$code" ]; then
            echo "MODE=$c_mode"
            echo "PROVIDER=$c_prov"
            echo "NET=$c_net"
            echo "OPTS=$c_opts"
            echo "DESC=$c_desc"
            return 0
        fi
    done
    return 1
}

apply_opts() {
    local opts="$1"
    [ -z "$opts" ] && return 0
    local dns_file="$HOME/dnsmasq/floxin_dns.py"
    [ ! -f "$dns_file" ] && return 1
    IFS=',' read -ra pairs <<< "$opts"
    for p in "${pairs[@]}"; do
        local k=$(echo "$p" | cut -d= -f1)
        local v=$(echo "$p" | cut -d= -f2)
        case "$k" in
            cache)
                case "$v" in
                    big) sed -i 's/^CACHE_TTL.*/CACHE_TTL = 3600/' "$dns_file" ;;
                    off) sed -i 's/^CACHE_TTL.*/CACHE_TTL = 0/' "$dns_file" ;;
                    *)   sed -i 's/^CACHE_TTL.*/CACHE_TTL = 600/' "$dns_file" ;;
                esac ;;
        esac
    done
}

list_codes() {
    echo ""
    echo -e "  ${CC}═══ Activation Codes ═══${CZ}"
    echo ""
    printf "  %-18s %-13s %-20s %s\n" "CODE" "MODE" "PROVIDER" "DESCRIPTION"
    printf "  %-18s %-13s %-20s %s\n" "──────────────────" "─────────────" "────────────────────" "──────────────────"
    for entry in "${CODES[@]}"; do
        IFS='|' read -r c_id c_mode c_prov c_net c_opts c_desc <<< "$entry"
        printf "  ${CG}%-18s${CZ} %-13s %-20s %s\n" "$c_id" "$c_mode" "$c_prov" "$c_desc"
    done
    echo ""
    echo -e "  ${CY}Use: code <CODE>  or type code directly${CZ}"
    echo ""
}
