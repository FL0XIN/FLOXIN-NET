package com.fl0xin.floxinnet.ui.env
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
@Composable fun EnvScreen(vm: EnvViewModel = viewModel()) { val info by vm.info.collectAsState(); val clipboard = LocalClipboardManager.current; Page(stringResource(R.string.env_title)) { PremiumCard(Modifier.fillMaxWidth()) { info.forEach { Text(it) } }; SecondaryButton(stringResource(R.string.copy_clipboard)) { clipboard.setText(AnnotatedString(info.joinToString("\n"))) }; Text(stringResource(R.string.recommendation)) } }
