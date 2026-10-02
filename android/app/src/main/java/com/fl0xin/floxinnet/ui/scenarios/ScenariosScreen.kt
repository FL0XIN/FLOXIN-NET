package com.fl0xin.floxinnet.ui.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*

@Composable
fun ScenariosScreen(vm: ScenariosViewModel = viewModel()) {
    val scenarios by vm.scenarios.collectAsState()
    Page(stringResource(R.string.scenarios_title)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scenarios, key = { it["code"].orEmpty() }) { scenario ->
                PremiumCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(scenario["code"].orEmpty(), style = MaterialTheme.typography.titleMedium)
                        Text("${scenario["type"]} · ${scenario["provider"]} · ${scenario["net"]}")
                        Text(scenario["desc"].orEmpty())
                        SecondaryButton(stringResource(R.string.apply)) { }
                    }
                }
            }
        }
    }
}
