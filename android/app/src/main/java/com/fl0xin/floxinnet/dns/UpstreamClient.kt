package com.fl0xin.floxinnet.dns

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.Socket

class UpstreamClient(
    private val config: DnsConfig,
    private val protectDatagram: ((DatagramSocket) -> Boolean)? = null,
    private val protectSocket: ((Socket) -> Boolean)? = null,
) {
    fun query(payload: ByteArray): ByteArray {
        var lastError: Exception? = null
        for (server in config.upstreams) {
            try {
                val response = queryUdp(server, payload)
                if (response.size >= 12 && !isTruncated(response)) return response
                if (response.size >= 12 && isTruncated(response)) return queryTcp(server, payload)
            } catch (error: Exception) {
                lastError = error
            }
        }
        throw DnsException.UpstreamUnavailable(lastError)
    }

    private fun queryUdp(server: UpstreamServer, payload: ByteArray): ByteArray {
        DatagramSocket().use { socket ->
            socket.soTimeout = UDP_TIMEOUT_MILLIS
            protectDatagram?.invoke(socket)
            socket.send(DatagramPacket(payload, payload.size, InetSocketAddress(server.host, server.port)))
            val buffer = ByteArray(4096)
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            return packet.data.copyOf(packet.length)
        }
    }

    private fun queryTcp(server: UpstreamServer, payload: ByteArray): ByteArray {
        Socket().use { socket ->
            protectSocket?.invoke(socket)
            socket.connect(InetSocketAddress(server.host, server.port), TCP_TIMEOUT_MILLIS)
            socket.soTimeout = TCP_TIMEOUT_MILLIS
            DataOutputStream(socket.getOutputStream()).use { output ->
                output.writeShort(payload.size)
                output.write(payload)
                output.flush()
            }
            DataInputStream(socket.getInputStream()).use { input ->
                val size = input.readUnsignedShort()
                if (size < 12 || size > 65535) throw DnsException.UpstreamUnavailable()
                return ByteArray(size).also { input.readFully(it) }
            }
        }
    }

    private fun isTruncated(response: ByteArray): Boolean = (response[2].toInt() and 0x02) != 0

    companion object {
        private const val UDP_TIMEOUT_MILLIS = 2500
        private const val TCP_TIMEOUT_MILLIS = 3500
    }
}
