package com.fl0xin.floxinnet.data

import android.content.Context

object BlocklistStore {
    fun load(context: Context, database: FloxinDatabase): BlocklistMatcher {
        check(BlocklistMatcher.verifyAsset(context)) { "Blocklist integrity check failed" }
        val matcher = BlocklistMatcher.loadAsset(context)
        val dao = database.blocklistDao()
        if (dao.count() != matcher.size) {
            dao.clear()
            matcher.domainsForPersistence().toList().chunked(1000).forEach { chunk ->
                dao.insertAll(chunk.map(::BlocklistEntry))
            }
        }
        return matcher
    }
}
