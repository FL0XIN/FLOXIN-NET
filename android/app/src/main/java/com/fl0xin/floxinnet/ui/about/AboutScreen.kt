package com.fl0xin.floxinnet.ui.about
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page
@Composable fun AboutScreen(nav:NavHostController,vm:AboutViewModel= hiltViewModel()){Page(stringResource(R.string.about_title)){Text("FLOXIN NET ${vm.version}");Text(stringResource(R.string.about_description));Text(stringResource(R.string.license));Text(stringResource(R.string.github));Text(stringResource(R.string.credits));Button(onClick={nav.navigate("developer")}){Text(stringResource(R.string.developer_mode))};Button(onClick={}){Text(stringResource(R.string.report_issue))}}}
