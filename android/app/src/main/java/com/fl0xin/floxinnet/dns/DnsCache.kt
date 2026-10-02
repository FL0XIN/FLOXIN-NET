package com.fl0xin.floxinnet.dns

import java.util.LinkedHashMap

class DnsCache(private val capacity: Int, private val defaultTtlSeconds: Long) {
    private data class Entry(val response: ByteArray, val expiresAtMillis: Long)
    private val entries = object : LinkedHashMap<String, Entry>(capacity, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>?): Boolean = size > capacity
    }

    @Synchronized
    fun get(key: String, nowMillis: Long = System.currentTimeMillis()): ByteArray? {
        val entry = entries[key] ?: return null
        if (entry.expiresAtMillis <= nowMillis) {
            entries.remove(key)
            return null
        }
        return entry.response.copyOf()
    }

    @Synchronized
    fun put(key: String, response: ByteArray, ttlSeconds: Long? = null, nowMillis: Long = System.currentTimeMillis()) {
        val ttl = (ttlSeconds ?: defaultTtlSeconds).coerceIn(1, 86400)
        entries[key] = Entry(response.copyOf(), nowMillis + ttl * 1000)
    }

    @Synchronized
    fun clear() = entries.clear()

    @Synchronized
    fun size(): Int = entries.size
}
