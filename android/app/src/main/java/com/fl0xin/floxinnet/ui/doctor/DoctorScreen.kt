package com.fl0xin.floxinnet.ui.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun DoctorScreen(vm:DoctorViewModel= viewModel()){val checks by vm.checks.collectAsState();Page("Doctor"){checks.forEach{Text("✓ $it",color=MaterialTheme.colorScheme.primary)};Text("Blocklist SHA-256: ${if(vm.blocklistOk)"OK" else "FAILED"}");Button(onClick={}){Text("Run All")}}}
