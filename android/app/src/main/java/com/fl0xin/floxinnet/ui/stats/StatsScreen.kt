package com.fl0xin.floxinnet.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*

@Composable
fun StatsScreen(vm: StatsViewModel = hiltViewModel()) {
    val total by vm.total.collectAsState(); val blocked by vm.blocked.collectAsState(); val cached by vm.cached.collectAsState(); val forwarded by vm.forwarded.collectAsState()
    val progress = remember(total, blocked) { if (total == 0) 0f else blocked.toFloat() / total }
    Page(stringResource(R.string.stats_title)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { StatTile(stringResource(R.string.queries), total.toString()); StatTile(stringResource(R.string.blocked), blocked.toString()) }
        PremiumCard(Modifier.fillMaxWidth()) { Text("${stringResource(R.string.cached)}: $cached"); Text("${stringResource(R.string.forwarded)}: $forwarded"); CustomProgress(progress) }
        Text(stringResource(R.string.chart_hint))
    }
}
