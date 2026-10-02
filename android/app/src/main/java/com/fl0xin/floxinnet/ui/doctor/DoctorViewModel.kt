package com.fl0xin.floxinnet.ui.doctor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fl0xin.floxinnet.data.BlocklistMatcher
import kotlinx.coroutines.flow.MutableStateFlow

class DoctorViewModel(private val app: Application) : AndroidViewModel(app) {
    val checks = MutableStateFlow(listOf("VPN permission", "DNS engine", "Blocklist integrity", "Room database", "Upstream DNS", "Cache", "Network", "Logging"))
    val blocklistOk = MutableStateFlow(BlocklistMatcher.verifyAsset(app))
    fun runAll() { blocklistOk.value = BlocklistMatcher.verifyAsset(app) }
}
