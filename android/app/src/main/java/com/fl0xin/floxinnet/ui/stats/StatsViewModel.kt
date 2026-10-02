package com.fl0xin.floxinnet.ui.stats
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
@HiltViewModel class StatsViewModel @Inject constructor():ViewModel(){val total=MutableStateFlow(0);val blocked=MutableStateFlow(0);val cached=MutableStateFlow(0);val forwarded=MutableStateFlow(0)}
