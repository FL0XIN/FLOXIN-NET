package com.fl0xin.floxinnet.ui.codes
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.fl0xin.floxinnet.data.readAssetArray
class CodesViewModel(app:Application):AndroidViewModel(app){val query=MutableStateFlow("");val codes=MutableStateFlow(readAssetArray(app,"codes.json"));fun search(value:String){query.value=value;codes.value=readAssetArray(getApplication(),"codes.json").filter{it["code"].orEmpty().contains(value.uppercase())||it["desc"].orEmpty().contains(value,ignoreCase=true)}}
}
