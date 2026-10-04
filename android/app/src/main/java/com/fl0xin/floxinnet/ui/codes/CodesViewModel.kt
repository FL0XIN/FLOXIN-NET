package com.fl0xin.floxinnet.ui.codes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fl0xin.floxinnet.data.readAssetArray
import kotlinx.coroutines.flow.MutableStateFlow

class CodesViewModel(app: Application) : AndroidViewModel(app) {
    val query = MutableStateFlow("")
    val codes = MutableStateFlow(readAssetArray(app, "codes.json"))
    val applied = MutableStateFlow<String?>(null)

    fun search(value: String) {
        query.value = value
        codes.value = readAssetArray(getApplication(), "codes.json").filter {
            it["code"].orEmpty().contains(value.uppercase()) ||
                it["desc"].orEmpty().contains(value, ignoreCase = true)
        }
    }

    fun apply(code: String) {
        if (code.isNotBlank()) applied.value = code
    }
}
