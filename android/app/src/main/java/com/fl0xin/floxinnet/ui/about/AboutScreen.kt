package com.fl0xin.floxinnet.ui.about
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
@Composable fun AboutScreen(nav: NavHostController, vm: AboutViewModel = hiltViewModel()) { val uri = LocalUriHandler.current; Page(stringResource(R.string.about_title)) { PremiumCard(Modifier.fillMaxWidth()) { Text("FLOXIN NET ${vm.version}", style = MaterialTheme.typography.titleLarge); Text(stringResource(R.string.about_description)); Text(stringResource(R.string.license)); Text(stringResource(R.string.github)); Text(stringResource(R.string.credits)) }; PrimaryButton(stringResource(R.string.developer_mode), Modifier.fillMaxWidth()) { nav.navigate("developer") }; SecondaryButton(stringResource(R.string.report_issue), Modifier.fillMaxWidth()) { uri.openUri("https://github.com/FL0XIN/FLOXIN-NET/issues") } } }
