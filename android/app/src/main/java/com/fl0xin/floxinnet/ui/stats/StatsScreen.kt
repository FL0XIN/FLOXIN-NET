package com.fl0xin.floxinnet.ui.stats
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun StatsScreen(vm:StatsViewModel= hiltViewModel()){val total by vm.total.collectAsState();val blocked by vm.blocked.collectAsState();val cached by vm.cached.collectAsState();val forwarded by vm.forwarded.collectAsState();Page("Statistics"){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){StatTile("Queries",total.toString());StatTile("Blocked",blocked.toString())};Text("Cached: $cached    Forwarded: $forwarded");LinearProgressIndicator(if(total==0)0f else blocked.toFloat()/total,Modifier.fillMaxWidth());Text("24-hour chart and top domains will populate as query history is recorded.")}}
