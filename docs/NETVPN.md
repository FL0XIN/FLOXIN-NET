# NETVPN

`NETVPN` discovers and ranks the live public relay catalog from the official VPN Gate API. It does not embed credentials or stale server addresses.

It is also available from the main interactive shell as `FLOXIN netvpn ...`.

```bash
NETVPN refresh
NETVPN list --limit 20
NETVPN list --country JP
NETVPN best
NETVPN countries
```

The Android companion app uses the same source and ranking rules. Public VPN Gate relays are volunteer-operated, may log operational metadata, and can be slow or unavailable. They must not be presented as anonymous or suitable for banking and sensitive accounts.

The connection engine belongs to the separate `android/vpn` application. Android `VpnService` alone is not an OpenVPN implementation; the app must use the OpenVPN 3 native bridge before it reports a real connection.

Source: https://www.vpngate.net/api/iphone/
