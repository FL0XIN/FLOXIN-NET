package com.fl0xin.floxinnet.ui.codes

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
fun CodesScreen(vm: CodesViewModel = viewModel()) {
    val codes by vm.codes.collectAsState()
    val query by vm.query.collectAsState()
    Page(stringResource(R.string.codes_title)) {
        OutlinedTextField(query, vm::search, label = { Text(stringResource(R.string.search_codes)) }, modifier = Modifier.fillMaxWidth())
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(codes, key = { it["code"].orEmpty() }) { code ->
                PremiumCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(code["code"].orEmpty(), style = MaterialTheme.typography.titleMedium)
                        Text("${code["mode"]} · ${code["provider"]} · ${code["net"]}")
                        Text(code["desc"].orEmpty())
                        SecondaryButton(stringResource(R.string.apply)) { }
                    }
                }
            }
        }
    }
}
