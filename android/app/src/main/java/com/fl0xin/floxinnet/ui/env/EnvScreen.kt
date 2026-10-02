package com.fl0xin.floxinnet.ui.env
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page
@Composable fun EnvScreen(vm:EnvViewModel= viewModel()){val info by vm.info.collectAsState();Page(stringResource(R.string.env_title)){info.forEach{Text(it)};Button(onClick={}){Text(stringResource(R.string.copy_clipboard))};Text(stringResource(R.string.recommendation))}}
