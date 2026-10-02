#!/usr/bin/env python3
# floxin_dns.py — DNS Server محلي مع Caching + Blocklist
import os
import sys
import time
import threading
import socket
from datetime import datetime
from dnslib import DNSRecord, RR, QTYPE, A, AAAA
from dnslib.server import DNSServer, BaseResolver, DNSLogger


class SilentLogger(DNSLogger):
    """Logger صامت — مش بيطبع أي حاجة."""
    def log_recv(self, handler, data): pass
    def log_send(self, handler, data): pass
    def log_request(self, handler, request): pass
    def log_reply(self, handler, reply): pass
    def log_truncated(self, handler, reply): pass
    def log_error(self, handler, e): pass
    def log_data(self, dnsobj): pass
    def log_prefix(self, handler): pass

# أوقف logging dnslib المزعج
import logging
logging.getLogger("dnslib").setLevel(logging.CRITICAL)
logging.getLogger("dnslib.server").setLevel(logging.CRITICAL)
logging.getLogger("dnslib").propagate = False

BASE = os.path.dirname(os.path.abspath(__file__))
BLOCKLIST_FILE = os.path.join(BASE, "blocklist.txt")
CACHE_TTL = 600
PORT = int(os.environ.get("FLOXIN_PORT", "5353"))
BIND_ADDRESS = os.environ.get("FLOXIN_BIND_ADDRESS", "127.0.0.1")

# ═══ Blocklist ═══
_BLOCKED = set()
_BLOCKED_LOADED = False

def load_blocklist():
    """يحمّل قائمة الدومينات المحجوبة."""
    global _BLOCKED, _BLOCKED_LOADED
    if _BLOCKED_LOADED:
        return
    if not os.path.isfile(BLOCKLIST_FILE):
        print("[dns] مفيش blocklist")
        _BLOCKED_LOADED = True
        return
    try:
        with open(BLOCKLIST_FILE, encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                parts = line.split()
                if len(parts) >= 2 and parts[0] in ("0.0.0.0", "127.0.0.1"):
                    dom = parts[1].lower().strip(".")
                    if dom and dom not in ("localhost",):
                        _BLOCKED.add(dom)
        print("[dns] blocklist: " + str(len(_BLOCKED)) + " دومين")
    except Exception as e:
        print("[dns] blocklist error: " + str(e))
    _BLOCKED_LOADED = True


# ═══ Query Logging ═══
QUERY_LOG = os.path.join(BASE, "queries.log")

def log_query(qname, qtype, action, result=""):
    """يسجل استعلام في queries.log مع rotation."""
    try:
        line = f"{time.strftime('%H:%M:%S')}|{qtype}|{action}|{qname}|{result}\n"
        # اقرأ الملف لو كبير
        if os.path.exists(QUERY_LOG) and os.path.getsize(QUERY_LOG) > 100000:
            with open(QUERY_LOG, encoding="utf-8", errors="ignore") as f:
                lines = f.readlines()
            with open(QUERY_LOG, "w", encoding="utf-8") as f:
                f.writelines(lines[-500:])
        with open(QUERY_LOG, "a", encoding="utf-8") as f:
            f.write(line)
    except Exception:
        pass


# دومينات نتجاهلها (لا نطبعها ولا نحجبها)
_QUIET_SUFFIXES = (
    ".in-addr.arpa", ".ip6.arpa", ".local", ".localdomain",
    "localhost", "local", "broadcasthost",
)


def _is_quiet(qname):
    """يشوف لو الاستعلام للـ PTR/localhost."""
    q = qname.lower()
    for suf in _QUIET_SUFFIXES:
        if q.endswith(suf) or q == suf.lstrip("."):
            return True
    # شيل أول نقطة
    if q.startswith("_") or q in ("0.0.0.0", "255.255.255.255"):
        return True
    return False


def is_blocked(qname):
    """يشوف لو الدومين محجوب (بما فيه subdomains)."""
    q = qname.lower().strip(".")
    if q in _BLOCKED:
        return True
    # شوف الأباء
    parts = q.split(".")
    for i in range(len(parts)):
        parent = ".".join(parts[i:])
        if parent in _BLOCKED:
            return True
    return False


# ═══ Cache ═══
_CACHE = {}
_CACHE_LOCK = threading.Lock()


def cache_get(qname, qtype):
    """يرجع من الـ cache لو موجود ولسه صالح."""
    key = (qname.lower(), qtype)
    with _CACHE_LOCK:
        entry = _CACHE.get(key)
        if not entry:
            return None
        if time.time() > entry["expires"]:
            _CACHE.pop(key, None)
            return None
        return entry["answer"]


def cache_set(qname, qtype, answer, ttl=300):
    """يحفظ في الـ cache."""
    ttl = max(60, min(ttl, CACHE_TTL))
    key = (qname.lower(), qtype)
    with _CACHE_LOCK:
        _CACHE[key] = {
            "answer": answer,
            "expires": time.time() + ttl,
        }
        # نمسح لو الـ cache كبر
        if len(_CACHE) > 5000:
            now = time.time()
            for k in [k for k, v in _CACHE.items() if v["expires"] < now]:
                _CACHE.pop(k, None)


# ═══ Upstream DNS ═══
UPSTREAM = ["94.140.14.14", "94.140.15.15"]
UPSTREAM_TIMEOUT = 3


def query_upstream(request, upstream_ip):
    """يبعت الاستعلام لـ DNS خارجي ويرجع الرد."""
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.settimeout(UPSTREAM_TIMEOUT)
        sock.sendto(request.pack(), (upstream_ip, 53))
        data, _ = sock.recvfrom(4096)
        sock.close()
        return DNSRecord.parse(data)
    except Exception as e:
        return None


# ═══ Resolver ═══
class FloxinResolver(BaseResolver):
    def __init__(self):
        self.stats = {"total": 0, "blocked": 0, "cached": 0, "forwarded": 0}

    def resolve(self, request, handler):
        qname = str(request.q.qname).rstrip(".")
        qtype = QTYPE[request.q.qtype]

        # تجاهل PTR/localhost بصمت (بدون عدّ)
        if _is_quiet(qname) or qtype in ("PTR", "SOA", "NS", "SRV", "TXT", "ANY"):
            reply = request.reply()
            return reply

        self.stats["total"] += 1

        # 1) محجوب؟
        if is_blocked(qname):
            self.stats["blocked"] += 1
            reply = request.reply()
            if qtype == "A":
                reply.add_answer(RR(qname, QTYPE.A, rdata=A("0.0.0.0"), ttl=60))
            elif qtype == "AAAA":
                reply.add_answer(RR(qname, QTYPE.AAAA, rdata=AAAA("::"), ttl=60))
            log_query(qname, qtype, "BLOCK", "0.0.0.0")
            return reply

        # 2) cache؟
        cached = cache_get(qname, qtype)
        if cached:
            self.stats["cached"] += 1
            reply = request.reply()
            try:
                for rr in cached:
                    reply.add_answer(rr)
                log_query(qname, qtype, "CACHE", str(rr) if cached else "")
                return reply
            except Exception:
                pass

        # 3) forward
        for up in UPSTREAM:
            resp = query_upstream(request, up)
            if resp and resp.header.rcode == 0:
                self.stats["forwarded"] += 1
                try:
                    _ans = str(resp.rr[0].rdata) if resp.rr else ""
                    log_query(qname, qtype, "OK", _ans)
                except Exception:
                    log_query(qname, qtype, "OK", "")
                # خزّن في الـ cache
                try:
                    rrs = list(resp.rr) + list(resp.auth)
                    ttl = 300
                    if rrs:
                        ttl = min(rr.ttl for rr in rrs) or 300
                    cache_set(qname, qtype, rrs, ttl)
                except Exception:
                    pass
                return resp

        # 4) فشل
        return request.reply()

    def show_stats(self):
        s = self.stats
        total = max(s["total"], 1)
        print("─" * 40)
        print("📊 إحصائيات DNS")
        print("  إجمالي:    " + str(s["total"]))
        print("  محجوب:     " + str(s["blocked"]) + " (" + str(round(s["blocked"]*100/total, 1)) + "%)")
        print("  من الكاش:  " + str(s["cached"]) + " (" + str(round(s["cached"]*100/total, 1)) + "%)")
        print("  forwarded: " + str(s["forwarded"]))
        print("  cache size: " + str(len(_CACHE)))
        print("─" * 40)


# ═══ Main ═══
def main():
    print("═" * 50)
    print("FLOXIN DNS Server")
    print("═" * 50)
    print()

    # حمّل blocklist
    load_blocklist()

    # جيب IP التليفون
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        local_ip = s.getsockname()[0]
        s.close()
    except Exception:
        local_ip = "127.0.0.1"

    resolver = FloxinResolver()

    # Bind locally by default. Use FLOXIN_BIND_ADDRESS=0.0.0.0 only when
    # intentionally exposing the resolver to a trusted network.
    servers = []
    try:
        srv = DNSServer(resolver, port=PORT, address=BIND_ADDRESS, logger=SilentLogger())
        srv.start_thread()
        servers.append(srv)
        print("✓ يستمع على " + BIND_ADDRESS + ":" + str(PORT))
    except Exception as e:
        print("✗ " + BIND_ADDRESS + ":" + str(PORT) + " — " + str(e))

    if not servers:
        print()
        print("✗ فشل تشغيل السيرفر")
        sys.exit(1)

    print()
    print("═" * 50)
    print("  ✅ DNS شغال")
    print("═" * 50)
    print("  العنوان:   " + BIND_ADDRESS + ":" + str(PORT))
    print("  cache:    " + str(CACHE_TTL) + "s")
    print("  محجوب:    " + str(len(_BLOCKED)) + " دومين")
    print("═" * 50)
    print()
    print("اضغط Ctrl+C للإيقاف")
    print()

    # stats كل دقيقة
    def _stats_loop():
        while True:
            time.sleep(60)
            resolver.show_stats()

    threading.Thread(target=_stats_loop, daemon=True).start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print()
        print("إيقاف...")
        resolver.show_stats()
        for s in servers:
            try:
                s.stop()
            except Exception:
                pass
        print("✓ تم")


if __name__ == "__main__":
    main()
