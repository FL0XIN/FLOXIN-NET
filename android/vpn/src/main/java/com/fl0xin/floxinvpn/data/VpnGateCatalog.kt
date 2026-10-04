package com.fl0xin.floxinvpn.data

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class VpnGateCatalog(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofSeconds(20))
        .build()
) {
    suspend fun fetch(): List<VpnServer> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://www.vpngate.net/api/iphone/")
            .header("User-Agent", "FLOXIN-VPN/0.1 (+https://github.com/FL0XIN/FLOXIN-NET)")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("VPN Gate HTTP ${response.code}")
            parse(response.body?.string().orEmpty())
        }
    }

    internal fun parse(csv: String): List<VpnServer> {
        val lines = csv.lineSequence().map(String::trim).filter(String::isNotEmpty).toList()
        val headerLine = lines.firstOrNull { it.startsWith("#HostName,") } ?: return emptyList()
        val header = headerLine.removePrefix("#").split(',')
        val index = header.withIndex().associate { it.value to it.index }
        fun field(row: List<String>, key: String) = row.getOrNull(index[key] ?: -1).orEmpty()
        return lines.asSequence().filter { !it.startsWith("#") }.mapNotNull { line ->
            // The final field is Base64 and never contains commas. Preserve Message as one field.
            val profileSeparator = line.lastIndexOf(',')
            if (profileSeparator <= 0) return@mapNotNull null
            val row = line.substring(0, profileSeparator).split(',', limit = header.size - 1) + line.substring(profileSeparator + 1)
            runCatching {
                VpnServer(
                    hostName = field(row, "HostName"),
                    ip = field(row, "IP"),
                    country = field(row, "CountryLong"),
                    countryCode = field(row, "CountryShort"),
                    pingMs = field(row, "Ping").toLongOrNull() ?: Long.MAX_VALUE,
                    speedBps = field(row, "Speed").toLongOrNull() ?: 0L,
                    sessions = field(row, "NumVpnSessions").toIntOrNull() ?: Int.MAX_VALUE,
                    score = field(row, "Score").toLongOrNull() ?: 0L,
                    logPolicy = field(row, "LogType"),
                    openVpnProfileBase64 = field(row, "OpenVPN_ConfigData_Base64")
                ).takeIf { it.usable }
            }.getOrNull()
        }.toList()
    }

    fun decodeProfile(server: VpnServer): String =
        Base64.decode(server.openVpnProfileBase64, Base64.DEFAULT).toString(Charsets.UTF_8)
}
