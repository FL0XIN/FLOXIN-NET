package com.fl0xin.floxinnet.ui.logs
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun LogsScreen(vm:LogsViewModel= hiltViewModel()){val logs by vm.logs.collectAsState();Page("Live Logs"){Text("Filter: All · Blocked · Allowed · Cached");if(logs.isEmpty())Text("No DNS queries recorded yet.") else logs.forEach{Text(it)}}}
