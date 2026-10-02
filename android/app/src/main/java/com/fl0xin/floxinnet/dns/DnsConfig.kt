package com.fl0xin.floxinnet.dns

import android.content.Context
import com.fl0xin.floxinnet.data.ProviderCatalog

data class UpstreamServer(val host: String, val port: Int = 53)

data class DnsConfig(
    val upstreams: List<UpstreamServer> = DEFAULT_UPSTREAMS,
    val cacheTtlSeconds: Long = 600,
    val cacheCapacity: Int = 512,
) {
    companion object {
        private val DEFAULT_UPSTREAMS = listOf(UpstreamServer("1.1.1.1"), UpstreamServer("8.8.8.8"))

        fun load(context: Context): DnsConfig {
            val preferences = context.getSharedPreferences("floxin_dns", Context.MODE_PRIVATE)
            val provider = preferences.getString("provider", "Cloudflare").orEmpty()
            val record = ProviderCatalog.find(context, provider)
            val configured = preferences.getString("upstream_servers", null)
                ?.split(',')
                ?.mapNotNull { value -> value.trim().takeIf(String::isNotEmpty)?.let(::UpstreamServer) }
                ?.takeIf { it.isNotEmpty() }
            val upstreams = configured ?: record?.let { listOf(UpstreamServer(it.primary), UpstreamServer(it.secondary)) } ?: DEFAULT_UPSTREAMS
            return DnsConfig(
                upstreams = upstreams,
                cacheTtlSeconds = preferences.getLong("cache_ttl_seconds", 600).coerceIn(30, 86400),
                cacheCapacity = preferences.getInt("cache_capacity", 512).coerceIn(32, 4096),
            )
        }
    }
}
