package com.fl0xin.floxinnet.ui.providers
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.fl0xin.floxinnet.data.readAssetArray
class ProvidersViewModel(app:Application):AndroidViewModel(app){val providers=MutableStateFlow(readAssetArray(app,"providers.json"));val selected=MutableStateFlow("Cloudflare")}
