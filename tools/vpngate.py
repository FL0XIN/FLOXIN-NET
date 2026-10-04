#!/usr/bin/env python3
"""NETVPN: inspect live VPN Gate public relays without embedding credentials or stale servers."""
import argparse, base64, csv, json, sys, time
from pathlib import Path
from urllib.request import Request, urlopen

API = "https://www.vpngate.net/api/iphone/"
FIELDS = ["HostName", "IP", "Score", "Ping", "Speed", "CountryLong", "CountryShort", "NumVpnSessions", "Uptime", "TotalUsers", "TotalTraffic", "LogType", "Operator", "Message", "OpenVPN_ConfigData_Base64"]
CACHE = Path.home() / ".floxin_vpn" / "catalog.json"

def fetch():
    req = Request(API, headers={"User-Agent": "FLOXIN-NET/1 NETVPN (+https://github.com/FL0XIN/FLOXIN-NET)"})
    with urlopen(req, timeout=20) as response:
        text = response.read().decode("utf-8", "replace")
    lines = [line.strip() for line in text.splitlines() if line.strip()]
    header_line = next((line for line in lines if line.startswith("#HostName,")), None)
    if not header_line:
        raise RuntimeError("VPN Gate catalog header not found")
    header = header_line[1:].split(",")
    items = []
    for line in lines[lines.index(header_line) + 1:]:
        if line.startswith("#"):
            continue
        profile_at = line.rfind(",")
        if profile_at <= 0:
            continue
        row = next(csv.reader([line[:profile_at]])) + [line[profile_at + 1:]]
        if len(row) != len(header):
            continue
        item = dict(zip(header, row))
        if not item.get("IP") or not item.get("OpenVPN_ConfigData_Base64"):
            continue
        items.append(item)
    CACHE.parent.mkdir(parents=True, exist_ok=True)
    CACHE.write_text(json.dumps({"source": API, "updated_at": int(time.time()), "servers": items}, indent=2), encoding="utf-8")
    return items

def ranked(items, country=None):
    def number(item, key, default=0):
        try: return int(item.get(key, default))
        except ValueError: return default
    if country:
        items = [x for x in items if x.get("CountryShort", "").lower() == country.lower() or x.get("CountryLong", "").lower() == country.lower()]
    return sorted(items, key=lambda x: (-min(number(x, "Speed"), 500_000_000), number(x, "Ping", 999999), number(x, "NumVpnSessions", 999999), -number(x, "Score")))

def output(items):
    safe = []
    for x in items:
        safe.append({k: x.get(k, "") for k in ("HostName", "IP", "CountryLong", "CountryShort", "Ping", "Speed", "NumVpnSessions", "Score", "LogType")})
    print(json.dumps({"source": API, "warning": "Volunteer public relays are not private or guaranteed stable.", "servers": safe}, ensure_ascii=False, indent=2))

def main():
    parser = argparse.ArgumentParser(prog="NETVPN", description="Rank live public VPN Gate relays; connection requires the OpenVPN 3 app engine.")
    sub = parser.add_subparsers(dest="command", required=True)
    for name, help_text in (("list", "list top live public relays"), ("best", "show the best current relay")):
        p = sub.add_parser(name, help=help_text); p.add_argument("--country", help="country code or name"); p.add_argument("--limit", type=int, default=20)
    p = sub.add_parser("countries", help="list countries currently represented"); p.add_argument("--refresh", action="store_true")
    p = sub.add_parser("refresh", help="refresh the local catalog cache")
    args = parser.parse_args()
    items = fetch()
    if args.command == "refresh":
        print(json.dumps({"cached": str(CACHE), "servers": len(items), "source": API}, indent=2)); return
    if args.command == "countries":
        print(json.dumps(sorted({x.get("CountryLong", "") for x in items if x.get("CountryLong")}), ensure_ascii=False, indent=2)); return
    result = ranked(items, getattr(args, "country", None))
    output(result[:1 if args.command == "best" else max(1, min(args.limit, 100))])

if __name__ == "__main__":
    try: main()
    except Exception as exc:
        print(f"NETVPN: {exc}", file=sys.stderr); raise SystemExit(1)
