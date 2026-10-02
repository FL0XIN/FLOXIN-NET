package com.fl0xin.floxinnet.ui.providers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page

@Composable
fun ProvidersScreen(vm: ProvidersViewModel = viewModel()) {
    val providers by vm.providers.collectAsState()
    val selected by vm.selected.collectAsState()
    Page(stringResource(R.string.providers_title)) {
        Text(stringResource(R.string.current, selected))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(providers) { provider ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(provider["name"].orEmpty(), style = MaterialTheme.typography.titleMedium)
                            Text("${provider["primary"]} · ${provider["region"]}")
                        }
                        RadioButton(provider["name"] == selected, { vm.selected.value = provider["name"].orEmpty() })
                    }
                }
            }
        }
    }
}
