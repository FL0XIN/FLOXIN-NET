package com.fl0xin.floxinnet.ui.logs
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
@HiltViewModel class LogsViewModel @Inject constructor():ViewModel(){val filter=MutableStateFlow("All");val logs=MutableStateFlow(emptyList<String>())}
