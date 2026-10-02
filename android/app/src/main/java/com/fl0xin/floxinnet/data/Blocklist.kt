package com.fl0xin.floxinnet.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest

class BlocklistMatcher private constructor(private val domains: HashSet<String>) {
    val size: Int get() = domains.size
    fun domainsForPersistence(): Set<String> = domains.toSet()

    fun isBlocked(domain: String): Boolean {
        var candidate = domain.lowercase().trim().trimEnd('.')
        while (candidate.isNotEmpty()) {
            if (candidate in domains) return true
            val separator = candidate.indexOf('.')
            if (separator < 0) break
            candidate = candidate.substring(separator + 1)
        }
        return false
    }

    companion object {
        fun loadAsset(context: Context): BlocklistMatcher {
            val domains = HashSet<String>(90_000)
            context.assets.open(BLOCKLIST_ASSET).use { stream ->
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).useLines { lines ->
                    lines.forEach { line ->
                        val parts = line.trim().split(Regex("\\s+"))
                        if (parts.size >= 2 && (parts[0] == "0.0.0.0" || parts[0] == "127.0.0.1")) {
                            val domain = parts[1].lowercase().trimEnd('.')
                            if (domain.contains('.') && domain !in IGNORED_HOSTS) domains.add(domain)
                        }
                    }
                }
            }
            return BlocklistMatcher(domains)
        }

        fun verifyAsset(context: Context): Boolean {
            val actual = sha256(context, BLOCKLIST_ASSET)
            val expected = context.assets.open(HASH_ASSET).bufferedReader().use { it.readText().trim().split(Regex("\\s+"))[0] }
            return actual.equals(expected, ignoreCase = true)
        }

        private fun sha256(context: Context, asset: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            context.assets.open(asset).use { stream ->
                val buffer = ByteArray(8192)
                var count: Int
                while (stream.read(buffer).also { count = it } >= 0) if (count > 0) digest.update(buffer, 0, count)
            }
            return digest.digest().joinToString("") { "%02x".format(it) }
        }

        private const val BLOCKLIST_ASSET = "blocklist.txt"
        private const val HASH_ASSET = "blocklist.sha256"
        private val IGNORED_HOSTS = setOf("localhost", "localhost.localdomain", "broadcasthost")
    }
}
