package com.fl0xin.floxinvpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fl0xin.floxinvpn.data.VpnGateCatalog
import com.fl0xin.floxinvpn.data.VpnRanking
import com.fl0xin.floxinvpn.data.VpnServer
import com.fl0xin.floxinvpn.vpn.FloxinVpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StartupGate { FloxinVpnApp() } }
    }
}

@Composable
private fun StartupGate(content: @Composable () -> Unit) {
    var ready by remember { mutableStateOf(false) }
    var skipped by remember { mutableStateOf(false) }
    var elapsed by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (elapsed < 30 && !skipped) { delay(1000); elapsed++ }
        ready = true
    }
    if (ready || skipped) content() else {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF070B14)) {
            Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
                Text("FLOXIN VPN", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF00D9FF))
                Spacer(Modifier.height(12.dp))
                Text("Preparing the live server catalog", color = Color.LightGray)
                Text("This startup check may take up to 30 seconds.", color = Color.Gray)
                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(progress = { elapsed / 30f }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("${30 - elapsed}s remaining", color = Color.Gray)
                Spacer(Modifier.height(18.dp))
                OutlinedButton(onClick = { skipped = true }) { Text("Skip") }
            }
        }
    }
}

private data class CountrySummary(val country: String, val code: String, val servers: Int, val best: VpnServer)

@Composable
private fun FloxinVpnApp() {
    val context = LocalContext.current
    val catalog = remember { VpnGateCatalog() }
    val scope = rememberCoroutineScope()
    var servers by remember { mutableStateOf<List<VpnServer>>(emptyList()) }
    var selected by remember { mutableStateOf<VpnServer?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("Choose a country or let FLOXIN select automatically.") }
    var connected by remember { mutableStateOf(false) }
    val vpnConsent = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            message = "VPN permission granted. The native OpenVPN engine is required to establish the tunnel."
            connected = false
        } else message = "VPN permission was cancelled."
    }

    suspend fun refresh() {
        loading = true; error = null
        runCatching { withContext(Dispatchers.IO) { catalog.fetch() } }
            .onSuccess { selected = null; servers = VpnRanking.rank(it); message = "${servers.size} live relays loaded." }
            .onFailure { error = it.message ?: "Could not load public servers" }
        loading = false
    }
    LaunchedEffect(Unit) { refresh() }

    val countries = remember(servers) { servers.groupBy { it.countryCode.ifBlank { it.country.ifBlank { "??" } } }.map { (_, list) -> val best = VpnRanking.rank(list).first(); CountrySummary(best.country.ifBlank { "Unknown country" }, best.countryCode, list.size, best) }.sortedByDescending { it.best.speedBps } }
    val best = servers.firstOrNull()

    fun connect() {
        val target = selected ?: best
        if (target == null) { message = "No live relay is available yet."; return }
        selected = target
        message = "Best relay selected: ${target.displayName}. Requesting VPN permission…"
        VpnService.prepare(context)?.let(vpnConsent::launch) ?: run {
            context.startService(Intent(context, FloxinVpnService::class.java).setAction(FloxinVpnService.ACTION_START))
            connected = false
            message = "Relay selected. OpenVPN 3 native engine is not bundled in this build yet."
        }
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF070B14)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("FLOXIN VPN", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF00D9FF))
                Text("Provider: VPN Gate public relays", color = Color.LightGray)
                Text("More catalog providers can be added only with a documented API and profile source.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (connected) "CONNECTED" else "READY", style = MaterialTheme.typography.titleLarge, color = if (connected) Color(0xFF00E676) else Color(0xFF00D9FF))
                        Text(message, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { connect() }, modifier = Modifier.fillMaxWidth(), enabled = !loading && !connected) { Text(if (selected == null) "Connect automatically" else "Connect to ${selected!!.country}") }
                        if (connected) OutlinedButton(onClick = { connected = false }, modifier = Modifier.fillMaxWidth()) { Text("Disconnect") }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (loading) "Updating…" else "${countries.size} countries · ${servers.size} relays")
                    OutlinedButton(onClick = { scope.launch { refresh() } }, enabled = !loading) { Text("Refresh") }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                if (loading && countries.isEmpty()) CircularProgressIndicator()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(countries, key = { it.code + it.country }) { summary ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("${summary.country} (${summary.code})", style = MaterialTheme.typography.titleMedium)
                                    Text("${summary.servers} relays · ${summary.best.pingMs} ms · ${summary.best.speedBps / 1_000_000} Mbps", style = MaterialTheme.typography.bodySmall)
                                }
                                OutlinedButton(onClick = { selected = summary.best; message = "${summary.country} selected. Press Connect to continue." }) { Text("Select") }
                            }
                        }
                    }
                }
            }
        }
    }
}
