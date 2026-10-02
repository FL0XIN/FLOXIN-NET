package com.fl0xin.floxinnet.ui.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
import com.fl0xin.floxinnet.ui.theme.Success
@Composable fun DoctorScreen(vm: DoctorViewModel = viewModel()) { val checks by vm.checks.collectAsState(); val ok by vm.blocklistOk.collectAsState(); Page(stringResource(R.string.doctor_title)) { PremiumCard { checks.forEach { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Check, contentDescription = null, tint = Success); Text(it) } }; Text(stringResource(R.string.blocklist_sha, if (ok) "OK" else "FAILED")) }; PrimaryButton(stringResource(R.string.doctor_run_all), onClick = vm::runAll) } }
