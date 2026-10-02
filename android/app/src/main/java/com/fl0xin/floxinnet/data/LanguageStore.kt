package com.fl0xin.floxinnet.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first

object LanguageStore {
    val languages = listOf("auto", "en", "ar", "zh", "ru", "ja", "ko", "hi", "th", "vi", "id", "fr", "de", "es", "it", "pt", "nl", "pl", "tr", "pt-BR", "es-MX", "es-AR")
    fun apply(tag: String) { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(if (tag == "auto") "" else tag)) }
    suspend fun current(context: Context): String = context.settingsDataStore.data.first()[SettingKeys.language] ?: "auto"
    fun save(context: Context, tag: String) {
        CoroutineScope(Dispatchers.IO).launch { context.settingsDataStore.edit { it[SettingKeys.language] = tag } }
        apply(tag)
    }
}
