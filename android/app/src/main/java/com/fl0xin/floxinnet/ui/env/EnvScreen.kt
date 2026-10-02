package com.fl0xin.floxinnet.ui.env
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun EnvScreen(vm:EnvViewModel= viewModel()){val info by vm.info.collectAsState();Page("Environment"){info.forEach{Text(it)};Button(onClick={}){Text("Copy to clipboard")};Text("Recommendation: keep VPN notification enabled for reliable DNS service.")}}
