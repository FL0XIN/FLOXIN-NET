package com.fl0xin.floxinnet.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fl0xin.floxinnet.data.LanguageStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("floxin_dns", 0)
    val provider = MutableStateFlow(prefs.getString("provider", "Cloudflare")!!)
    val logging = MutableStateFlow(prefs.getBoolean("logging", true))
    val mode = MutableStateFlow(prefs.getString("mode", "booster")!!)
    val language = MutableStateFlow("auto")
    init { viewModelScope.launch { language.value = LanguageStore.current(app) } }
    fun saveProvider(value: String) { provider.value = value; prefs.edit().putString("provider", value).apply() }
    fun saveLogging(value: Boolean) { logging.value = value; prefs.edit().putBoolean("logging", value).apply() }
    fun saveMode(value: String) { mode.value = value; prefs.edit().putString("mode", value).apply() }
    fun saveLanguage(value: String) { language.value = value; LanguageStore.save(app, value) }
}
