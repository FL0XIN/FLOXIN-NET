package com.fl0xin.floxinnet.ui.codes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.components.Page

@Composable
fun CodesScreen(vm: CodesViewModel = viewModel()) {
    val codes by vm.codes.collectAsState()
    val query by vm.query.collectAsState()
    Page("Activation Codes") {
        OutlinedTextField(query, vm::search, label = { Text("Search 100 codes") }, modifier = Modifier.fillMaxWidth())
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(codes) { code ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(code["code"].orEmpty(), style = MaterialTheme.typography.titleMedium)
                        Text("${code["mode"]} · ${code["provider"]} · ${code["net"]}")
                        Text(code["desc"].orEmpty())
                        TextButton(onClick = {}) { Text("Apply") }
                    }
                }
            }
        }
    }
}
