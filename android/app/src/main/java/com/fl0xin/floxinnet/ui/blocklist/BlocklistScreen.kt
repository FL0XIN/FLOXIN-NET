package com.fl0xin.floxinnet.ui.blocklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun BlocklistScreen(vm:BlocklistViewModel= viewModel()){val count by vm.count.collectAsState();val ok by vm.hashOk.collectAsState();Page("Blocklist"){Text("Domains: $count",style=MaterialTheme.typography.headlineSmall);Text("SHA-256: ${vm.hash}");Text("Integrity: ${if(ok)"Verified" else "Failed"}");Text("Last update: bundled asset");Button(onClick={}){Text("Update")};OutlinedButton(onClick={}){Text("Clear Cache")}}}
