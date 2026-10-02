package com.fl0xin.floxinnet.ui.blocklist
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
@Composable fun BlocklistScreen(vm: BlocklistViewModel = viewModel()) { val count by vm.count.collectAsState(); val ok by vm.hashOk.collectAsState(); Page(stringResource(R.string.blocklist_title)) { PremiumCard { Text(stringResource(R.string.domains, count), style = MaterialTheme.typography.titleLarge); Text(stringResource(R.string.blocklist_sha, vm.hash)); Text(stringResource(if (ok) R.string.integrity_verified else R.string.integrity_failed)); Text(stringResource(R.string.last_update)) }; PrimaryButton(stringResource(R.string.update), onClick = vm::refresh); SecondaryButton(stringResource(R.string.clear_cache), onClick = vm::refresh) } }
