package com.fl0xin.floxinnet.ui.doctor
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.fl0xin.floxinnet.data.BlocklistMatcher
class DoctorViewModel(app:Application):AndroidViewModel(app){val checks=MutableStateFlow(listOf("VPN permission","DNS engine","Blocklist integrity","Room database","Upstream DNS","Cache","Network","Logging"));val blocklistOk=BlocklistMatcher.verifyAsset(app)}
