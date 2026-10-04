# FLOXIN VPN

Independent international VPN application inside the FLOXIN NET repository.

## Current implementation

The module has a distinct package (`com.fl0xin.floxinvpn`) and application identity. It fetches the official VPN Gate public relay CSV, parses OpenVPN profile metadata, ranks candidates by measured speed, latency, sessions, and score, and exposes a conservative limited-data policy for Egyptian mobile plans.

The app does **not** embed credentials or stale server addresses. Public relay data is refreshed at runtime. The app also does not claim a connection until the real OpenVPN 3 session reports an established tunnel.

## Native tunnel boundary

The `FloxinVpnService` class is intentionally only the ownership boundary at this stage. The next implementation step is the OpenVPN 3 JNI/TUN bridge. A fake `VpnService.Builder` interface is not acceptable because it would show a VPN icon without forwarding encrypted traffic.

OpenVPN 3 is available under MPL 2.0 or AGPLv3. The planned integration uses the MPL 2.0 option where permitted, preserves the corresponding notices, and keeps original FLOXIN code under MIT. It must not be silently relicensed as MIT.

## Data-saving profile

The default policy is conservative: MTU 1280, MSS fix 1200, keepalive 45 seconds, compression disabled, and Cloudflare DNS. Compression is disabled because VPN compression can increase risk and CPU usage; it is not a guaranteed data-saving feature. Video and downloads still consume their normal data.

## Public relay warning

VPN Gate relays are operated by volunteers. Relay speed, uptime, location, and logging policy vary. The app should show the operator/logging warning before connection and should not recommend public relays for banking, sensitive accounts, or private work.

Source: https://www.vpngate.net/api/iphone/
