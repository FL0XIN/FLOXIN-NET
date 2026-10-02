package com.fl0xin.floxinnet.ui.settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun SettingsScreen(vm:SettingsViewModel= viewModel()){val provider by vm.provider.collectAsState();val logging by vm.logging.collectAsState();Page("Settings"){Text("Mode");Row{RadioButton(true,{});Text("Booster");RadioButton(false,{});Text("Saver");RadioButton(false,{});Text("2X1")};Text("Network: WiFi / SIM / Both");Text("Theme: Dark (default)");Text("Cache TTL: 600 seconds");Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Logging");Switch(logging,vm::saveLogging)};Text("Provider: $provider");Button(onClick={}){Text("Update Blocklist")}}}
