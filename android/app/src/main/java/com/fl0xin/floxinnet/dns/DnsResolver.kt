package com.fl0xin.floxinnet.dns

import com.fl0xin.floxinnet.data.BlocklistMatcher

class DnsResolver(
    config: DnsConfig,
    private val blocklist: BlocklistMatcher? = null,
    protectDatagram: ((java.net.DatagramSocket) -> Boolean)? = null,
    protectSocket: ((java.net.Socket) -> Boolean)? = null,
) {
    private val cache = DnsCache(config.cacheCapacity, config.cacheTtlSeconds)
    private val upstream = UpstreamClient(config, protectDatagram, protectSocket)

    fun resolve(query: ByteArray): ByteArray = resolve("", query)

    fun resolve(queryName: String, query: ByteArray): ByteArray {
        validateQuery(query)
        if (queryName.isNotBlank() && blocklist?.isBlocked(queryName) == true) return blockedResponse(query)
        val key = cacheKey(query)
        val cached = cache.get(key)
        if (cached != null) return withTransactionId(cached, transactionId(query))
        val response = upstream.query(query)
        validateResponse(response)
        val normalized = withTransactionId(response, 0)
        cache.put(key, normalized, ttlSeconds(response))
        return withTransactionId(normalized, transactionId(query))
    }

    fun clearCache() = cache.clear()
    fun cacheSize(): Int = cache.size()

    private fun validateQuery(payload: ByteArray) {
        if (payload.size < 12) throw DnsException.MalformedQuery()
        val flags = read16(payload, 2)
        if ((flags and 0x8000) != 0 || read16(payload, 4) != 1) throw DnsException.MalformedQuery()
    }

    private fun validateResponse(payload: ByteArray) {
        if (payload.size < 12 || (read16(payload, 2) and 0x8000) == 0) throw DnsException.UpstreamUnavailable()
    }

    private fun blockedResponse(query: ByteArray): ByteArray {
        val end = questionEnd(query)
        val type = read16(query, end - 4)
        val answerBytes = when (type) { 1 -> 16; 28 -> 28; else -> 0 }
        val response = ByteArray(end + answerBytes)
        query.copyInto(response, 0, 0, end)
        response[2] = 0x81.toByte()
        response[3] = 0x80.toByte()
        response[6] = if (answerBytes > 0) 0 else 0
        response[7] = if (answerBytes > 0) 1 else 0
        if (answerBytes > 0) {
            write16(response, end, 0xc00c)
            write16(response, end + 2, type)
            write16(response, end + 4, 1)
            write32(response, end + 6, 60)
            write16(response, end + 10, if (type == 1) 4 else 16)
            if (type == 1) response[end + 12] = 0
            else response[end + 12] = 0
        }
        return response
    }

    private fun questionEnd(payload: ByteArray): Int {
        var cursor = 12
        while (cursor < payload.size) {
            val length = payload[cursor].toInt() and 0xff
            cursor++
            if (length == 0) return cursor + 4
            if (length > 63 || cursor + length > payload.size) throw DnsException.MalformedQuery()
            cursor += length
        }
        throw DnsException.MalformedQuery()
    }

    private fun cacheKey(payload: ByteArray): String = payload.copyOf().also {
        it[0] = 0; it[1] = 0; it[2] = 0; it[3] = 0
    }.joinToString("") { "%02x".format(it) }

    private fun ttlSeconds(response: ByteArray): Long {
        if (response.size < 12) return 60
        val answerCount = read16(response, 6)
        var cursor = 12
        while (cursor < response.size && response[cursor].toInt() != 0) {
            val length = response[cursor].toInt() and 0xff
            if (length > 63 || cursor + length >= response.size) return 60
            cursor += length + 1
        }
        cursor += 5
        repeat(answerCount) {
            if (cursor + 12 > response.size) return 60
            cursor += if ((response[cursor].toInt() and 0xc0) == 0xc0) 2 else {
                while (cursor < response.size && response[cursor].toInt() != 0) cursor += (response[cursor].toInt() and 0xff) + 1
                cursor + 1
            }
            if (cursor + 10 > response.size) return 60
            val ttl = read32(response, cursor + 4)
            cursor += 10 + read16(response, cursor + 8)
            return ttl.coerceIn(1L, 86400L)
        }
        return 60
    }

    private fun transactionId(payload: ByteArray): Int = read16(payload, 0)
    private fun withTransactionId(payload: ByteArray, id: Int): ByteArray = payload.copyOf().also { write16(it, 0, id) }
    private fun read16(bytes: ByteArray, offset: Int): Int = ((bytes[offset].toInt() and 0xff) shl 8) or (bytes[offset + 1].toInt() and 0xff)
    private fun read32(bytes: ByteArray, offset: Int): Long = (read16(bytes, offset).toLong() shl 16) or read16(bytes, offset + 2).toLong()
    private fun write16(bytes: ByteArray, offset: Int, value: Int) { bytes[offset] = (value ushr 8).toByte(); bytes[offset + 1] = value.toByte() }
    private fun write32(bytes: ByteArray, offset: Int, value: Int) { write16(bytes, offset, value ushr 16); write16(bytes, offset + 2, value) }
}
