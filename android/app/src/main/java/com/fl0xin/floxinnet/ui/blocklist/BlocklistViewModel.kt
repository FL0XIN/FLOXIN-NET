package com.fl0xin.floxinnet.ui.blocklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fl0xin.floxinnet.data.BlocklistMatcher
import kotlinx.coroutines.flow.MutableStateFlow

class BlocklistViewModel(private val app: Application) : AndroidViewModel(app) {
    val count = MutableStateFlow(72234)
    val hashOk = MutableStateFlow(BlocklistMatcher.verifyAsset(app))
    val hash = "c90ebedf5453c551b8e5c3f164640db5defb79228fd7ba3d7b58b5f7804a421a"
    fun refresh() { hashOk.value = BlocklistMatcher.verifyAsset(app); count.value = 72234 }
}
