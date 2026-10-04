package com.fl0xin.floxinvpn.data

/** A public volunteer relay from VPN Gate. Never treat these as trusted private infrastructure. */
data class VpnServer(
    val hostName: String,
    val ip: String,
    val country: String,
    val countryCode: String,
    val pingMs: Long,
    val speedBps: Long,
    val sessions: Int,
    val score: Long,
    val logPolicy: String,
    val openVpnProfileBase64: String
) {
    val displayName: String get() = "$country ($countryCode)"
    val usable: Boolean get() = openVpnProfileBase64.isNotBlank() && ip.isNotBlank()
}

data class VpnCatalogState(
    val loading: Boolean = false,
    val servers: List<VpnServer> = emptyList(),
    val error: String? = null,
    val updatedAt: Long? = null
)

data class ConnectionPolicy(
    val mtu: Int = 1280,
    val mssFix: Int = 1200,
    val compression: Boolean = false,
    val keepAliveSeconds: Int = 30,
    val dns: List<String> = listOf("1.1.1.1", "1.0.0.1"),
    val routeAllTraffic: Boolean = true
)
