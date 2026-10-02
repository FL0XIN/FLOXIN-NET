#!/usr/bin/env python3
"""NETTURBO: measure alternate CDN ports and persist conservative recommendations."""
import argparse, asyncio, json, os, sqlite3, subprocess, sys, time
from pathlib import Path

PORTS=(443,8443,2053,2083,2087,2096,8080,8880)
BASE=Path(os.environ.get("FLOXIN_NETTURBO_DIR", Path.home()/".floxin_netturbo")); BASE.mkdir(parents=True,exist_ok=True)
CONFIG=BASE/"config.json"; DB=BASE/"history.db"; LOG=BASE/"log.txt"; ENV=BASE/"environment.sh"; PID=BASE/"monitor.pid"
# All normal measurements use the same public CDN and download path so results are comparable.
# The environment override is retained only for explicit local diagnostics.
DEFAULT_CDN_HOST="speed.cloudflare.com"
HOST=os.environ.get("FLOXIN_NETTURBO_HOST",DEFAULT_CDN_HOST)
CDN_PATH="/__down"
BYTES=int(os.environ.get("FLOXIN_NETTURBO_BYTES","500000"))

def config():
    try:return json.loads(CONFIG.read_text())
    except Exception:return {}
def save_config(data): CONFIG.write_text(json.dumps(data,indent=2)); return data
def db():
    c=sqlite3.connect(DB); c.execute("CREATE TABLE IF NOT EXISTS samples (id INTEGER PRIMARY KEY, ts REAL, port INTEGER, speed_bps REAL, latency_ms REAL, ok INTEGER, error TEXT)"); c.commit(); return c
def log(msg):
    with LOG.open("a") as f:f.write(f"{time.strftime('%Y-%m-%d %H:%M:%S')} {msg}\n")
def run_curl(port):
    url=f"https://{HOST}:{port}{CDN_PATH}?bytes={BYTES}"
    start=time.perf_counter()
    cmd=["curl","-L","-sS","--fail","--connect-timeout","3","--max-time","12","-o","/dev/null","-w","%{speed_download}",url]
    try:
        r=subprocess.run(cmd,capture_output=True,text=True,timeout=15)
        elapsed=(time.perf_counter()-start)*1000
        if r.returncode!=0: return {"port":port,"ok":False,"speed_bps":0.0,"latency_ms":round(elapsed,2),"error":r.stderr.strip()[-160:] or f"curl exit {r.returncode}"}
        return {"port":port,"ok":True,"speed_bps":float(r.stdout.strip() or 0),"latency_ms":round(elapsed,2),"error":""}
    except Exception as e:return {"port":port,"ok":False,"speed_bps":0.0,"latency_ms":None,"error":str(e)}
async def scan_port(port,tries):
    results=[]
    for _ in range(tries): results.append(await asyncio.to_thread(run_curl,port))
    ok=[x for x in results if x["ok"]]
    return {"port":port,"tries":tries,"successes":len(ok),"speed_bps":round(sum(x["speed_bps"] for x in ok)/len(ok),2) if ok else 0.0,"latency_ms":round(sum(x["latency_ms"] for x in ok)/len(ok),2) if ok else None,"stability":round(len(ok)/tries*100,1),"errors":[x["error"] for x in results if x["error"]][:2]}
async def scan(ports=PORTS,tries=3): return await asyncio.gather(*(scan_port(p,tries) for p in ports))
def record(results):
    c=db(); ts=time.time()
    for r in results:c.execute("INSERT INTO samples(ts,port,speed_bps,latency_ms,ok,error) VALUES(?,?,?,?,?,?)",(ts,r["port"],r["speed_bps"],r["latency_ms"],int(r["successes"]>0),"; ".join(r["errors"])))
    c.commit(); c.close()
def winner(results):
    valid=[r for r in results if r["successes"] and r["speed_bps"]>0]
    return max(valid,key=lambda r:(r["speed_bps"],r["stability"])) if valid else None
def emit(obj): print(json.dumps(obj,indent=2,ensure_ascii=False))
def scan_cmd(args):
    results=asyncio.run(scan(tries=args.tries)); record(results); w=winner(results); log(f"scan winner={w['port'] if w else 'none'}")
    emit({"cdn":{"host":HOST,"path":CDN_PATH,"bytes":BYTES,"fixed_default":HOST==DEFAULT_CDN_HOST},"tries":args.tries,"ports":results,"winner":w})
def best_cmd(_):
    c=db(); row=c.execute("SELECT port,AVG(speed_bps) speed,AVG(latency_ms) latency,AVG(ok)*100 stability FROM samples WHERE ts>? GROUP BY port ORDER BY speed DESC LIMIT 1",(time.time()-3600,)).fetchone(); c.close()
    emit({"cdn":{"host":HOST,"path":CDN_PATH,"fixed_default":HOST==DEFAULT_CDN_HOST},"best":({"port":row[0],"speed_bps":round(row[1],2),"latency_ms":round(row[2],2) if row[2] else None,"stability":round(row[3],1)} if row else None)})
def apply_cmd(args):
    if args.port not in PORTS: raise SystemExit(f"unsupported port; choose one of {','.join(map(str,PORTS))}")
    result=apply_port(args.port)
    result["note"]="This is a client setting; it does not transparently reroute all system traffic."
    emit(result)
def apply_port(port):
    d=config(); d.update({"host":HOST,"active_port":port,"updated_at":time.strftime("%Y-%m-%dT%H:%M:%SZ",time.gmtime())}); save_config(d)
    ENV.write_text(f"# Source this file to use the selected endpoint in compatible clients\nexport HTTPS_PORT={port}\nexport FLOXIN_NETTURBO_PORT={port}\n")
    log(f"applied port={port}"); return {"applied":True,"port":port,"config":str(CONFIG),"environment":str(ENV)}
def stop_monitor(_):
    if not PID.exists(): emit({"stopped":False,"reason":"monitor is not running"}); return
    try: os.kill(int(PID.read_text()),15)
    except (OSError,ValueError): pass
    PID.unlink(missing_ok=True); emit({"stopped":True})
def status(_):
    running=False
    if PID.exists():
        try: os.kill(int(PID.read_text()),0); running=True
        except (OSError,ValueError): PID.unlink(missing_ok=True)
    emit({"directory":str(BASE),"host":HOST,"active_port":config().get("active_port"),"monitor_running":running,"monitor_pid":PID.read_text().strip() if running else None,"config":config()})
def history(args):
    c=db(); rows=c.execute("SELECT datetime(ts,'unixepoch','localtime'),port,ROUND(speed_bps,2),ROUND(latency_ms,2),ok FROM samples WHERE ts>? ORDER BY ts DESC LIMIT 500",(time.time()-args.hours*3600,)).fetchall(); c.close(); emit({"hours":args.hours,"rows":[{"time":r[0],"port":r[1],"speed_bps":r[2],"latency_ms":r[3],"ok":bool(r[4])} for r in rows]})
def report(_):
    c=db(); rows=c.execute("SELECT port,AVG(speed_bps),AVG(latency_ms),AVG(ok)*100,COUNT(*) FROM samples GROUP BY port ORDER BY AVG(speed_bps) DESC").fetchall(); c.close(); best=rows[0][1] if rows else 0
    emit({"cdn":{"host":HOST,"path":CDN_PATH,"fixed_default":HOST==DEFAULT_CDN_HOST},"ports":[{"port":r[0],"avg_speed_bps":round(r[1],2),"avg_latency_ms":round(r[2],2) if r[2] else None,"stability":round(r[3],1),"samples":r[4],"relative_to_best_percent":round(r[1]/best*100,1) if best else None} for r in rows],"estimated_gain":"Measurement only; savings depend on the application and network policy.","recommendation":"Apply only after reviewing repeated measurements."})
def auto_cmd(_):
    if PID.exists():
        try:
            pid=int(PID.read_text()); os.kill(pid,0); emit({"started":False,"already_running":True,"pid":pid}); return
        except (OSError,ValueError): PID.unlink(missing_ok=True)
    p=subprocess.Popen([sys.executable,__file__,"monitor"],stdin=subprocess.DEVNULL,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL,start_new_session=True)
    PID.write_text(str(p.pid)); log(f"auto started pid={p.pid}"); emit({"started":True,"pid":p.pid,"interval_seconds":300,"storage":str(BASE)})
def monitor(args):
    while True:
        results=asyncio.run(scan(tries=1)); record(results); w=winner(results)
        if w:
            current=config().get("active_port")
            switched=current != w["port"]
            if switched: apply_port(w["port"])
            log(f"monitor winner={w['port']} speed_bps={w['speed_bps']} switched={switched}")
            print(f"NETTURBO monitor: best port {w['port']} at {w['speed_bps']:.0f} B/s" + (" (selected)" if switched else ""),flush=True)
        if args.once: break
        time.sleep(args.interval)
def main():
    p=argparse.ArgumentParser(prog="NETTURBO",description=f"Measure alternate HTTPS ports on fixed CDN {DEFAULT_CDN_HOST}{CDN_PATH}")
    s=p.add_subparsers(dest="cmd",required=True)
    q=s.add_parser("scan",help="test all eight ports with repeated downloads"); q.add_argument("--tries",type=int,default=3); q.set_defaults(fn=scan_cmd)
    s.add_parser("best",help="show the best recent port").set_defaults(fn=best_cmd)
    q=s.add_parser("apply",help="save a selected client port"); q.add_argument("--port",type=int,required=True); q.set_defaults(fn=apply_cmd)
    q=s.add_parser("monitor",help="repeat measurements"); q.add_argument("--interval",type=int,default=300); q.add_argument("--once",action="store_true"); q.set_defaults(fn=monitor)
    s.add_parser("auto",help="start detached five-minute monitoring").set_defaults(fn=auto_cmd)
    s.add_parser("status",help="show current selected port").set_defaults(fn=status)
    s.add_parser("stop",help="stop detached monitoring").set_defaults(fn=stop_monitor)
    q=s.add_parser("history",help="show recent measurements"); q.add_argument("--hours",type=int,default=24); q.set_defaults(fn=history)
    s.add_parser("report",help="show aggregate report").set_defaults(fn=report)
    a=p.parse_args(); a.fn(a)
if __name__=="__main__": main()
