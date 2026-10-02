package com.fl0xin.floxinnet.data

import android.content.Context
import org.json.JSONArray

fun readAssetArray(context: Context, name: String): List<Map<String, String>> {
    return runCatching {
        val array = JSONArray(context.assets.open(name).bufferedReader().use { it.readText() })
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            item.keys().asSequence().associateWith { key -> item.getString(key) }
        }
    }.getOrDefault(emptyList())
}
