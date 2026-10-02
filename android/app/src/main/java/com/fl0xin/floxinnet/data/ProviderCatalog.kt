package com.fl0xin.floxinnet.data

import android.content.Context
import org.json.JSONArray

data class ProviderRecord(val name: String, val primary: String, val secondary: String, val region: String)

object ProviderCatalog {
    fun all(context: Context): List<ProviderRecord> = runCatching {
        val array = JSONArray(context.assets.open("providers.json").bufferedReader().use { it.readText() })
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            ProviderRecord(item.getString("name"), item.getString("primary"), item.getString("secondary"), item.getString("region"))
        }
    }.getOrDefault(emptyList())

    fun find(context: Context, name: String): ProviderRecord? =
        all(context).firstOrNull { it.name.equals(name, ignoreCase = true) }
}
