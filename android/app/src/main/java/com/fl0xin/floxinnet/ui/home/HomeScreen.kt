package com.fl0xin.floxinnet.ui.home
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.ui.components.*
import com.fl0xin.floxinnet.ui.theme.CyberBlue
import com.fl0xin.floxinnet.vpn.FloxinVpnService
@Composable fun HomeScreen(vm:HomeViewModel= hiltViewModel()){val context=LocalContext.current;val running by vm.running.collectAsState();val launcher=rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){if(it.resultCode==Activity.RESULT_OK){ContextCompat.startForegroundService(context,Intent(context,FloxinVpnService::class.java).setAction(FloxinVpnService.ACTION_START));vm.refresh()}};Page("FLOXIN NET"){Text(if(running)"DNS protection is running" else "DNS protection is stopped");Button(onClick={if(running){context.startService(Intent(context,FloxinVpnService::class.java).setAction(FloxinVpnService.ACTION_STOP));vm.refresh()}else{VpnService.prepare(context)?.let(launcher::launch)?:run{ContextCompat.startForegroundService(context,Intent(context,FloxinVpnService::class.java));vm.refresh()}}},modifier=Modifier.fillMaxWidth()){Text(if(running)"Stop DNS" else "Start DNS")};Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){StatTile("Blocked","—");StatTile("Cached","—");StatTile("Forwarded","—")};Text("Mode: DATA_SAVER",color=CyberBlue);Text("Quick access: SAVE-MAX · BOOST-CF · BLOCK-ADS")}}
