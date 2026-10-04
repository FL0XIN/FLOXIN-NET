package com.fl0xin.floxinvpn.data

object VpnRanking {
    fun rank(servers: List<VpnServer>, country: String? = null): List<VpnServer> =
        servers.asSequence()
            .filter { it.usable && (country.isNullOrBlank() || it.countryCode.equals(country, true)) }
            .sortedWith(
                compareByDescending<VpnServer> { it.speedBps.coerceAtMost(500_000_000L) }
                    .thenBy { it.pingMs }
                    .thenBy { it.sessions }
                    .thenByDescending { it.score }
            )
            .take(80)
            .toList()

    /** Conservative settings for limited data plans; compression is intentionally disabled. */
    fun limitedDataPolicy(): ConnectionPolicy = ConnectionPolicy(
        mtu = 1280,
        mssFix = 1200,
        compression = false,
        keepAliveSeconds = 45,
        dns = listOf("1.1.1.1", "1.0.0.1")
    )
}
