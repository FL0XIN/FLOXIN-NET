#!/usr/bin/env python3
"""NETOPT: measurement-first network recommendations; system settings are not changed implicitly."""
import argparse, json, os, re, socket, subprocess, time
from pathlib import Path
STATE=Path(os.environ.get("FLOXIN_NETOPT_DIR",Path.home()/".floxin_netopt")); STATE.mkdir(parents=True,exist_ok=True)
STATE_FILE=STATE/"state.json"

def load():
    try:return json.loads(STATE_FILE.read_text())
    except Exception:return {}
def save(d): STATE_FILE.write_text(json.dumps(d,indent=2,ensure_ascii=False)); return d
def emit(d): print(json.dumps(d,indent=2,ensure_ascii=False))
def run(cmd):
    try:
        r=subprocess.run(cmd,capture_output=True,text=True,timeout=5); return r.returncode,r.stdout.strip(),r.stderr.strip()
    except Exception as e:return 1,"",str(e)
def default_route():
    rc,out,err=run(["ip","route","get","1.1.1.1"])
    dev=re.search(r"\bdev\s+(\S+)",out); src=re.search(r"\bsrc\s+(\S+)",out)
    return {"interface":dev.group(1) if dev else None,"source":src.group(1) if src else None,"raw":out or err}
def ports(args):
    target=args.target; results=[]
    for port in args.ports:
        t=time.perf_counter()
        try:
            with socket.create_connection((target,port),timeout=args.timeout): ok=True; err=""
        except Exception as e: ok=False; err=str(e)
        results.append({"port":port,"reachable":ok,"latency_ms":round((time.perf_counter()-t)*1000,2) if ok else None,"error":err if not ok else None})
    emit({"target":target,"results":results,"note":"TCP reachability only; no traffic modification."})
def mtu_probe(_):
    route=default_route(); mtu=None
    if route["interface"]:
        rc,out,_=run(["ip","link","show","dev",route["interface"]]); m=re.search(r"mtu\s+(\d+)",out); mtu=int(m.group(1)) if m else None
    emit({"interface":route["interface"],"current_mtu":mtu,"recommended_start":min(mtu or 1500,1500),"method":"read-only interface inspection; packet-size probing is not forced"})
def mtu_set(args):
    state=load(); state["mtu_proposal"]={"value":args.value,"saved_at":time.strftime("%Y-%m-%dT%H:%M:%SZ",time.gmtime()),"applied":False}; save(state)
    emit({"saved":True,"value":args.value,"applied":False,"reason":"No root or interface mutation requested; use the OS network manager to apply after review."})
def tcp_tuning(_):
    vals={}
    for key in ("tcp_congestion_control","tcp_available_congestion_control","tcp_rmem","tcp_wmem"):
        p=Path("/proc/sys/net/ipv4")/key
        if p.exists(): vals[key]=p.read_text().strip()
    emit({"current":vals,"recommendation":"Keep the kernel default unless a measured workload justifies a change; NETOPT does not write sysctl values."})
def dns_optimize(_):
    candidates=[("cloudflare","1.1.1.1"),("google","8.8.8.8"),("quad9","9.9.9.9")]; results=[]
    for name,host in candidates:
        t=time.perf_counter()
        try: socket.getaddrinfo(host,None); ok=True; err=""
        except Exception as e: ok=False; err=str(e)
        results.append({"provider":name,"endpoint":host,"local_resolution_ms":round((time.perf_counter()-t)*1000,2) if ok else None,"reachable":ok,"error":err if not ok else None})
    emit({"results":results,"note":"This measures local resolution of provider addresses, not recursive DNS latency. Use DNSPICK for provider benchmarking."})
def protocol(args):
    outv=[]
    for mode,port in (("tcp",443),("udp",443),("quic",443)):
        if args.mode not in ("all",mode): continue
        t=time.perf_counter()
        try:
            s=socket.socket(socket.AF_INET,socket.SOCK_DGRAM if mode in ("udp","quic") else socket.SOCK_STREAM); s.settimeout(2)
            if mode in ("udp","quic"): s.sendto(b"NETOPT probe",(args.target,port)); ok=True
            else: s.connect((args.target,port)); ok=True
            s.close(); err=""
        except Exception as e: ok=False; err=str(e)
        outv.append({"mode":mode,"reachable":ok,"elapsed_ms":round((time.perf_counter()-t)*1000,2) if ok else None,"error":err if not ok else None})
    emit({"target":args.target,"results":outv,"note":"UDP send success is not proof of application-layer delivery."})
def route_test(args):
    emit({"target":args.target,"route":default_route()})
def status(_): emit({"state_dir":str(STATE),"state":load(),"route":default_route()})
def reset(_):
    if STATE_FILE.exists(): STATE_FILE.unlink()
    emit({"reset":True,"note":"Only NETOPT proposals were cleared; OS network settings were not changed."})
def auto(args):
    print("NETOPT auto: measurement-only profile")
    mtu_probe(args); dns_optimize(args); ports(argparse.Namespace(target=args.target,ports=[443,8443,2053],timeout=2))
def main():
    p=argparse.ArgumentParser(prog="NETOPT",description="Safe network optimization recommendations"); s=p.add_subparsers(dest="cmd",required=True)
    s.add_parser("status").set_defaults(fn=status); s.add_parser("reset").set_defaults(fn=reset); s.add_parser("mtu-probe").set_defaults(fn=mtu_probe); s.add_parser("tcp-tuning").set_defaults(fn=tcp_tuning); s.add_parser("dns-optimize").set_defaults(fn=dns_optimize)
    q=s.add_parser("mtu-set"); q.add_argument("--value",type=int,required=True); q.set_defaults(fn=mtu_set)
    q=s.add_parser("port-test"); q.add_argument("--target",default="1.1.1.1"); q.add_argument("--ports",default="443,8443,2053"); q.add_argument("--timeout",type=float,default=2); q.set_defaults(fn=lambda a:(setattr(a,"ports",[int(x) for x in a.ports.split(",")]),ports(a))[1])
    q=s.add_parser("route-test"); q.add_argument("--target",default="1.1.1.1"); q.set_defaults(fn=route_test)
    q=s.add_parser("protocol"); q.add_argument("--target",default="1.1.1.1"); q.add_argument("--mode",choices=["all","tcp","udp","quic"],default="all"); q.set_defaults(fn=protocol)
    q=s.add_parser("auto"); q.add_argument("--target",default="1.1.1.1"); q.set_defaults(fn=auto)
    a=p.parse_args(); a.fn(a)
if __name__=="__main__": main()
