package com.fl0xin.floxinnet.ui.scenarios
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.fl0xin.floxinnet.data.readAssetArray
class ScenariosViewModel(app:Application):AndroidViewModel(app){val scenarios=MutableStateFlow(readAssetArray(app,"scenarios.json"));val applied=MutableStateFlow<String?>(null);fun apply(code:String){if(code.isNotBlank()) applied.value=code}}
