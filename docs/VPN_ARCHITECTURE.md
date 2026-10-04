# FLOXIN VPN Architecture

FLOXIN VPN is a separate Android application from the local DNS app. It uses a distinct package, UI, service boundary, and release artifact while sharing the repository and project documentation.

## Public server source

The first catalog source is the official VPN Gate public relay API:

`https://www.vpngate.net/api/iphone/`

It provides live relay metadata and Base64-encoded OpenVPN profiles. The catalog is treated as untrusted, volatile input. Profiles are not embedded in the APK, and stale IPs are not committed to the repository.

## Selection pipeline

1. Fetch the catalog over HTTPS.
2. Parse only complete OpenVPN entries with an address and profile.
3. Rank by throughput, ping, sessions, and provider score.
4. Measure a candidate before use.
5. Show location, latency, throughput, sessions, and logging policy.
6. Ask Android for VPN consent.
7. Start the OpenVPN 3 native session only after the JNI/TUN bridge is ready.
8. Mark connected only after the native session reports success.

## Limited-data policy

The initial profile uses MTU 1280, MSS fix 1200, a 45-second keepalive, compression disabled, and a stable DNS pair. These values reduce some overhead and idle chatter but cannot guarantee lower data usage. The user can disable full-tunnel routing or change DNS only after an explicit warning about leak and compatibility trade-offs.

## Important boundary

Android `VpnService` creates the TUN interface but does not implement OpenVPN by itself. A real app needs an OpenVPN protocol engine. OpenVPN 3 is selected for the native bridge and its MPL 2.0/AGPLv3 licensing must remain visible in notices. FLOXIN original code remains MIT; third-party code and public relay data retain their own terms.

## Safety and privacy

Public volunteer relays can be logged, throttled, interrupted, or operated by parties FLOXIN does not control. The app must warn users and must not market this mode as anonymous, private, or guaranteed free.
