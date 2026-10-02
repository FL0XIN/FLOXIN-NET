#!/usr/bin/env python3
"""NETSCAN: safe network measurements; no packet injection or root privileges required."""
import argparse, json, os, socket, statistics, subprocess, sys, time
from pathlib import Path


def out(data):
    if isinstance(data, dict):
        print(json.dumps(data, indent=2, ensure_ascii=False))
    else:
        print(data)


def tcp_probe(target, port=443, timeout=2.0):
    start = time.perf_counter()
    try:
        with socket.create_connection((target, port), timeout=timeout):
            return (time.perf_counter() - start) * 1000, True, ""
    except Exception as exc:
        return None, False, str(exc)


def samples(target, count=5, interval=0.15, port=443):
    values, errors = [], []
    for i in range(max(1, count)):
        value, ok, err = tcp_probe(target, port)
        if ok and value is not None: values.append(value)
        else: errors.append(err)
        if i + 1 < count: time.sleep(interval)
    return values, errors


def stats(values, total=None):
    total = total if total is not None else len(values)
    return {"samples": total, "received": len(values), "loss_percent": round((1-len(values)/total)*100, 2) if total else 0.0,
            "min_ms": round(min(values), 2) if values else None,
            "avg_ms": round(statistics.mean(values), 2) if values else None,
            "p50_ms": round(statistics.median(values), 2) if values else None,
            "p95_ms": round(statistics.quantiles(values, n=20)[18], 2) if len(values) >= 2 else (round(values[0],2) if values else None),
            "max_ms": round(max(values), 2) if values else None}


def network(_):
    route = "unknown"; iface = "unknown"
    try:
        for line in Path("/proc/net/route").read_text().splitlines()[1:]:
            parts=line.split()
            if len(parts) > 1 and parts[1] == "00000000": iface=parts[0]; break
    except OSError: pass
    try: local=socket.gethostbyname(socket.gethostname())
    except Exception: local="unknown"
    try:
        route_cmd=subprocess.run(["ip","route","show","default"],capture_output=True,text=True,timeout=2).stdout.strip()
        route=route_cmd or route
    except Exception: pass
    out({"interface":iface,"local_address":local,"default_route":route,"method":"/proc + ip route; no root"})


def quality(args):
    values, errors=samples(args.target, args.count, 0.15)
    result=stats(values,args.count); result.update({"target":args.target,"probe":"TCP connect :443","errors":errors[-3:]})
    if result["loss_percent"] == 0 and result["p95_ms"] is not None:
        result["assessment"]="good" if result["p95_ms"] < 150 else ("variable" if result["p95_ms"] < 500 else "poor")
    else: result["assessment"]="unreliable"
    out(result)


def latency(args):
    values, errors=samples(args.target,args.count,0.2)
    r=stats(values,args.count); r.update({"target":args.target,"probe":"TCP connect :443","errors":errors[-3:]}); out(r)


def jitter(args):
    deadline=time.monotonic()+max(1,args.seconds); values=[]
    while time.monotonic()<deadline: 
        value,ok,_=tcp_probe(args.target)
        if ok and value is not None: values.append(value)
        time.sleep(1)
    diffs=[abs(b-a) for a,b in zip(values,values[1:])]
    out({"target":args.target,"seconds":args.seconds,"samples":len(values),"avg_ms":round(statistics.mean(values),2) if values else None,"jitter_ms":round(statistics.mean(diffs),2) if diffs else None,"max_jitter_ms":round(max(diffs),2) if diffs else None})


def packet_loss(args):
    values, errors=samples(args.target,args.count,0.05)
    out({"target":args.target,"probe":"TCP connect :443","result":stats(values,args.count),"errors":errors[-3:]})


def dpi_probe(args):
    checks=[]
    for port in (80,443,8443):
        value,ok,err=tcp_probe(args.target,port,2)
        checks.append({"port":port,"reachable":ok,"latency_ms":round(value,2) if value else None,"error":err if not ok else None})
    out({"target":args.target,"checks":checks,"assessment":"inconclusive: reachability and latency alone cannot prove DPI; no evasion traffic was generated"})


def qos_detect(args):
    values,_=samples(args.target,max(10,args.count),0.1)
    r=stats(values,len(values) or args.count)
    spread=(r["p95_ms"]-r["p50_ms"]) if r["p95_ms"] is not None and r["p50_ms"] is not None else None
    r.update({"target":args.target,"spread_ms":round(spread,2) if spread is not None else None,"assessment":"variable scheduling suspected" if spread and spread>100 else "no strong QoS signal in sample","note":"This is a statistical indication, not proof of traffic shaping."})
    out(r)


def main():
    p=argparse.ArgumentParser(prog="NETSCAN",description="Safe network quality and behavior measurements")
    sub=p.add_subparsers(dest="cmd",required=True)
    sub.add_parser("network",help="show current interface and default route"); sub.choices["network"].set_defaults(fn=network)
    for name,fn,help_text in [("quality",quality,"summarize connection quality"),("latency",latency,"measure TCP latency"),("packet-loss",packet_loss,"measure failed probes"),("qos-detect",qos_detect,"look for latency variability")]:
        q=sub.add_parser(name,help=help_text); q.add_argument("--target",default="1.1.1.1"); q.add_argument("--count",type=int,default=5); q.set_defaults(fn=fn)
    j=sub.add_parser("jitter",help="measure latency jitter"); j.add_argument("--target",default="1.1.1.1"); j.add_argument("--seconds",type=int,default=30); j.set_defaults(fn=jitter)
    d=sub.add_parser("dpi-probe",help="compare ordinary TCP reachability; not an evasion tool"); d.add_argument("--target",default="1.1.1.1"); d.set_defaults(fn=dpi_probe)
    a=p.parse_args(); a.fn(a)
if __name__=="__main__": main()
