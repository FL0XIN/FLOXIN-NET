package com.fl0xin.floxinnet.ui.stats
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
@Composable fun StatsScreen(vm:StatsViewModel= hiltViewModel()){val total by vm.total.collectAsState();val blocked by vm.blocked.collectAsState();val cached by vm.cached.collectAsState();val forwarded by vm.forwarded.collectAsState();Page(stringResource(R.string.stats_title)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){StatTile(stringResource(R.string.queries),total.toString());StatTile(stringResource(R.string.blocked),blocked.toString())};Text("${stringResource(R.string.cached)}: $cached    ${stringResource(R.string.forwarded)}: $forwarded");LinearProgressIndicator(if(total==0)0f else blocked.toFloat()/total,Modifier.fillMaxWidth());Text(stringResource(R.string.chart_hint))}}
