package com.fl0xin.floxinnet.ui.logs
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page
@Composable fun LogsScreen(vm:LogsViewModel= hiltViewModel()){val logs by vm.logs.collectAsState();Page(stringResource(R.string.logs_title)){Text("${stringResource(R.string.filter_all)} · ${stringResource(R.string.filter_blocked)} · ${stringResource(R.string.filter_allowed)} · ${stringResource(R.string.filter_cached)}");if(logs.isEmpty())Text(stringResource(R.string.no_queries)) else logs.forEach{Text(it)}}}
