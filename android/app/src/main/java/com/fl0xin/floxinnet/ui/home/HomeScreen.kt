package com.fl0xin.floxinnet.ui.home

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
import com.fl0xin.floxinnet.ui.theme.CyberBlue
import com.fl0xin.floxinnet.vpn.FloxinVpnService

@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val running by vm.running.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ContextCompat.startForegroundService(context, Intent(context, FloxinVpnService::class.java).setAction(FloxinVpnService.ACTION_START))
            vm.refresh()
        }
    }
    Page(stringResource(R.string.home_title)) {
        PremiumCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusIndicator(running)
                Text(stringResource(if (running) R.string.status_running else R.string.status_stopped))
            }
            PrimaryButton(stringResource(if (running) R.string.stop_dns else R.string.start_dns), Modifier.fillMaxWidth()) {
                if (running) {
                    context.startService(Intent(context, FloxinVpnService::class.java).setAction(FloxinVpnService.ACTION_STOP))
                    vm.refresh()
                } else {
                    VpnService.prepare(context)?.let(launcher::launch) ?: run { ContextCompat.startForegroundService(context, Intent(context, FloxinVpnService::class.java)); vm.refresh() }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(stringResource(R.string.blocked), "—")
            StatTile(stringResource(R.string.cached), "—")
            StatTile(stringResource(R.string.forwarded), "—")
        }
        PremiumCard(Modifier.fillMaxWidth()) { Text(stringResource(R.string.mode_data_saver), color = CyberBlue); Text(stringResource(R.string.quick_access)) }
    }
}
