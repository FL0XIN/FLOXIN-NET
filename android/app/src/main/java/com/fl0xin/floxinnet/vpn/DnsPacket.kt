package com.fl0xin.floxinnet.vpn

import java.net.InetAddress

/** IPv4/IPv6 UDP DNS packet support used by the VPN transport. */
data class DnsQuery(
    val transactionId: Int,
    val sourceIp: ByteArray,
    val destinationIp: ByteArray,
    val sourcePort: Int,
    val dnsPayload: ByteArray,
    val questionEnd: Int,
    val name: String,
    val queryType: Int,
    val queryClass: Int,
)

object DnsPacket {
    fun parse(packet: ByteArray, length: Int = packet.size): DnsQuery? {
        if (length < 28) return null
        return when ((packet[0].toInt() ushr 4) and 0x0f) {
            4 -> parseIpv4(packet, length)
            6 -> parseIpv6(packet, length)
            else -> null
        }
    }

    fun buildResponse(query: DnsQuery, dnsPayload: ByteArray): ByteArray =
        if (query.sourceIp.size == 16) buildIpv6UdpResponse(query, dnsPayload)
        else buildIpv4UdpResponse(query, dnsPayload)

    /** Compatibility response for the phase-1 localhost smoke test. */
    fun buildResponse(query: DnsQuery): ByteArray {
        val local = query.name == "localhost" && query.queryClass == 1 && query.queryType == 1
        val dns = ByteArray(query.questionEnd + if (local) 16 else 0)
        write16(dns, 0, query.transactionId)
        write16(dns, 2, if (local) 0x8180 else 0x8183)
        write16(dns, 4, 1)
        write16(dns, 6, if (local) 1 else 0)
        query.dnsPayload.copyInto(dns, 12, 12, query.questionEnd)
        if (local) {
            write16(dns, query.questionEnd, 0xc00c)
            write16(dns, query.questionEnd + 2, 1)
            write16(dns, query.questionEnd + 4, 1)
            write32(dns, query.questionEnd + 6, 60)
            write16(dns, query.questionEnd + 10, 4)
            InetAddress.getByName("127.0.0.1").address.copyInto(dns, query.questionEnd + 12)
        }
        return buildResponse(query, dns)
    }

    private fun parseIpv4(packet: ByteArray, length: Int): DnsQuery? {
        val ihl = (packet[0].toInt() and 0x0f) * 4
        if (ihl < 20 || length < ihl + 20 || (packet[9].toInt() and 0xff) != 17) return null
        val udp = ihl
        return parseUdp(packet, length, udp, packet.copyOfRange(12, 16), packet.copyOfRange(16, 20))
    }

    private fun parseIpv6(packet: ByteArray, length: Int): DnsQuery? {
        if (length < 48 || (packet[6].toInt() and 0xff) != 17) return null
        return parseUdp(packet, length, 40, packet.copyOfRange(8, 24), packet.copyOfRange(24, 40))
    }

    private fun parseUdp(packet: ByteArray, length: Int, udp: Int, source: ByteArray, destination: ByteArray): DnsQuery? {
        if (length < udp + 20 || read16(packet, udp + 2) != 53) return null
        val dnsStart = udp + 8
        val dns = packet.copyOfRange(dnsStart, length)
        if (read16(dns, 4) != 1) return null
        var cursor = 12
        val labels = mutableListOf<String>()
        while (cursor < dns.size) {
            val labelLength = dns[cursor].toInt() and 0xff
            cursor++
            if (labelLength == 0) break
            if (labelLength > 63 || cursor + labelLength > dns.size) return null
            labels += String(dns, cursor, labelLength, Charsets.US_ASCII)
            cursor += labelLength
        }
        if (cursor + 4 > dns.size) return null
        return DnsQuery(read16(dns, 0), source, destination, read16(packet, udp), dns, cursor + 4,
            labels.joinToString(".").lowercase(), read16(dns, cursor), read16(dns, cursor + 2))
    }

    private fun buildIpv4UdpResponse(query: DnsQuery, dns: ByteArray): ByteArray {
        val udpLength = 8 + dns.size
        val packet = ByteArray(20 + udpLength)
        packet[0] = 0x45
        write16(packet, 2, packet.size)
        packet[8] = 64
        packet[9] = 17
        query.destinationIp.copyInto(packet, 12)
        query.sourceIp.copyInto(packet, 16)
        write16(packet, 20, 53)
        write16(packet, 22, query.sourcePort)
        write16(packet, 24, udpLength)
        dns.copyInto(packet, 28)
        write16(packet, 10, checksum(packet, 0, 20))
        write16(packet, 26, udpChecksum(packet, 20, udpLength, query.destinationIp, query.sourceIp))
        return packet
    }

    private fun buildIpv6UdpResponse(query: DnsQuery, dns: ByteArray): ByteArray {
        val udpLength = 8 + dns.size
        val packet = ByteArray(40 + udpLength)
        packet[0] = 0x60
        write16(packet, 4, udpLength)
        packet[6] = 17
        packet[7] = 64
        query.destinationIp.copyInto(packet, 8)
        query.sourceIp.copyInto(packet, 24)
        write16(packet, 40, 53)
        write16(packet, 42, query.sourcePort)
        write16(packet, 44, udpLength)
        dns.copyInto(packet, 48)
        write16(packet, 46, ipv6UdpChecksum(packet, 40, udpLength, query.destinationIp, query.sourceIp))
        return packet
    }

    private fun udpChecksum(packet: ByteArray, offset: Int, length: Int, source: ByteArray, destination: ByteArray): Int {
        var sum = 17 + length
        sum += wordSum(source) + wordSum(destination)
        sum += payloadSum(packet, offset, length)
        return finalizeChecksum(sum)
    }

    private fun ipv6UdpChecksum(packet: ByteArray, offset: Int, length: Int, source: ByteArray, destination: ByteArray): Int {
        var sum = wordSum(source) + wordSum(destination)
        sum += (length ushr 16) + (length and 0xffff) + 17
        sum += payloadSum(packet, offset, length)
        return finalizeChecksum(sum)
    }

    private fun wordSum(bytes: ByteArray): Int {
        var sum = 0
        var index = 0
        while (index < bytes.size) {
            sum += (bytes[index].toInt() and 0xff) shl 8
            if (index + 1 < bytes.size) sum += bytes[index + 1].toInt() and 0xff
            index += 2
        }
        return sum
    }

    private fun payloadSum(bytes: ByteArray, offset: Int, length: Int): Int {
        var sum = 0
        var index = offset
        while (index + 1 < offset + length) {
            sum += ((bytes[index].toInt() and 0xff) shl 8) or (bytes[index + 1].toInt() and 0xff)
            index += 2
        }
        if (index < offset + length) sum += (bytes[index].toInt() and 0xff) shl 8
        return sum
    }

    private fun checksum(bytes: ByteArray, offset: Int, length: Int): Int = finalizeChecksum(payloadSum(bytes, offset, length))

    private fun finalizeChecksum(value: Int): Int {
        var sum = value
        while (sum ushr 16 != 0) sum = (sum and 0xffff) + (sum ushr 16)
        return sum.inv() and 0xffff
    }

    private fun read16(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xff) shl 8) or (bytes[offset + 1].toInt() and 0xff)

    private fun write16(bytes: ByteArray, offset: Int, value: Int) {
        bytes[offset] = (value ushr 8).toByte()
        bytes[offset + 1] = value.toByte()
    }

    private fun write32(bytes: ByteArray, offset: Int, value: Int) {
        write16(bytes, offset, value ushr 16)
        write16(bytes, offset + 2, value)
    }
}
