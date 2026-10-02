package com.fl0xin.floxinnet.ui.env
import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
class EnvViewModel(app:Application):AndroidViewModel(app){val info=MutableStateFlow(listOf("Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})","Device: ${Build.MANUFACTURER} ${Build.MODEL}","App target SDK: 35","Min SDK: 26"))}
