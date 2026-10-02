# Phase 1 Network Analysis

## NETSCAN

`NETSCAN` performs measurement-only diagnostics without root or packet injection:

```bash
NETSCAN network
NETSCAN quality --target 1.1.1.1
NETSCAN latency --target 1.1.1.1
NETSCAN jitter --seconds 30
NETSCAN packet-loss --count 100
NETSCAN dpi-probe
NETSCAN qos-detect
```

The probe uses ordinary TCP connection timing when ICMP/raw sockets are unavailable. `dpi-probe` reports reachability differences only; it does not attempt to evade filtering, and a result is explicitly marked inconclusive because latency alone cannot prove DPI.

## NETOPT

`NETOPT` measures and records recommendations without silently changing system networking:

```bash
NETOPT auto
NETOPT mtu-probe
NETOPT mtu-set --value 1400
NETOPT tcp-tuning
NETOPT port-test --ports 443,8443,2053
NETOPT route-test --target 1.1.1.1
NETOPT dns-optimize
NETOPT protocol --mode tcp
NETOPT status
NETOPT reset
```

`mtu-set` stores a reversible proposal under `~/.floxin_netopt/state.json`; it does not write `sysctl`, alter routes, or mutate an interface. This keeps the tool usable without root and avoids making connectivity worse automatically.

The Python engines are installed under `~/.floxin/tools/` and the shell wrappers are installed under `~/bin/`.
