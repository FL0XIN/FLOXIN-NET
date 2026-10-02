package com.fl0xin.floxinnet.ui.doctor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page
@Composable fun DoctorScreen(vm:DoctorViewModel= viewModel()){val checks by vm.checks.collectAsState();Page(stringResource(R.string.doctor_title)){checks.forEach{Text("✓ $it",color=MaterialTheme.colorScheme.primary)};Text(stringResource(R.string.blocklist_sha,if(vm.blocklistOk)"OK" else "FAILED"));Button(onClick={}){Text(stringResource(R.string.doctor_run_all))}}}
