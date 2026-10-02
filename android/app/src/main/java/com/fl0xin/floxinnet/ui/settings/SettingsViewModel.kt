package com.fl0xin.floxinnet.ui.settings
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
class SettingsViewModel(app:Application):AndroidViewModel(app){private val prefs=app.getSharedPreferences("floxin_dns",0);val provider=MutableStateFlow(prefs.getString("provider","Cloudflare")!!);val logging=MutableStateFlow(prefs.getBoolean("logging",true));fun saveProvider(v:String){provider.value=v;prefs.edit().putString("provider",v).apply()}fun saveLogging(v:Boolean){logging.value=v;prefs.edit().putBoolean("logging",v).apply()}}
