package com.fl0xin.floxinnet.ui.about
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.ui.components.*
@Composable fun AboutScreen(vm:AboutViewModel= hiltViewModel()){Page("About"){Text("FLOXIN NET ${vm.version}");Text("Local DNS filtering and data-saving utility.");Text("License: MIT");Text("GitHub: github.com/FL0XIN/FLOXIN-NET");Text("Credits: WORM and contributors");Button(onClick={}){Text("Report issue")}}}
