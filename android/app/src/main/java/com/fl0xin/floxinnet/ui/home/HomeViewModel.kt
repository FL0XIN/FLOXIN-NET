package com.fl0xin.floxinnet.ui.home
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import com.fl0xin.floxinnet.vpn.FloxinVpnService
@HiltViewModel class HomeViewModel @Inject constructor():ViewModel(){val running=MutableStateFlow(FloxinVpnService.isRunning);val uptime=MutableStateFlow("—");fun refresh(){running.value=FloxinVpnService.isRunning}}
