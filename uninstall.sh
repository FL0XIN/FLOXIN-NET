#!/usr/bin/env bash
set -u
HOME_BIN="${HOME}/bin"; DNS_DIR="${HOME}/dnsmasq"
for name in FLOXIN FLOXIN-API NETGUARD NETSCAN NETOPT NETTURBO DNSMGR QUICKCHECK DNSPICK NETPROBE PRESAFE NETTHROTTLE NETCHECK NETMON NETSTAT NETKILL; do rm -f "$HOME_BIN/$name"; done
rm -f "$HOME/.floxin/tools/netscan.py" "$HOME/.floxin/tools/netopt.py" "$HOME/.floxin/tools/netturbo.py"
rm -f "$HOME/.floxin_codes.sh" "$HOME/.floxin_scenarios.sh" "$HOME/.floxin_simple.sh" "$HOME/.floxin_short.sh" "$HOME/.floxin_net.sh" "$HOME/.floxin_environment"
rm -f "$DNS_DIR/floxin_dns.py" "$DNS_DIR/.dns.pid" "$DNS_DIR/.dns.lock"
printf '%s\n' 'FLOXIN NET program files removed. User data in ~/dnsmasq (blocklist, logs, config, backups) was preserved.'
