package com.fl0xin.floxinvpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.fl0xin.floxinvpn.data.VpnGateCatalog
import com.fl0xin.floxinvpn.data.VpnRanking
import com.fl0xin.floxinvpn.data.VpnServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FloxinVpnApp() }
    }
}

private data class CountrySummary(val country: String, val code: String, val servers: Int, val best: VpnServer)

@Composable
private fun FloxinVpnApp() {
    val catalog = remember { VpnGateCatalog() }
    val scope = rememberCoroutineScope()
    var servers by remember { mutableStateOf<List<VpnServer>>(emptyList()) }
    var selected by remember { mutableStateOf<VpnServer?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun refresh() {
        loading = true
        error = null
        runCatching { withContext(Dispatchers.IO) { catalog.fetch() } }
            .onSuccess { selected = null; servers = VpnRanking.rank(it) }
            .onFailure { error = it.message ?: "Could not load public servers" }
        loading = false
    }
    LaunchedEffect(Unit) { refresh() }

    val countries = remember(servers) {
        servers.groupBy { it.countryCode.ifBlank { it.country.ifBlank { "??" } } }
            .map { (_, list) ->
                val best = VpnRanking.rank(list).first()
                CountrySummary(best.country.ifBlank { "Unknown country" }, best.countryCode, list.size, best)
            }
            .sortedByDescending { it.best.speedBps }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF070B14)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("FLOXIN VPN", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF00D9FF))
                Text("Unique countries · best relay per country · limited-data mode", color = Color.LightGray)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Data-saving profile", style = MaterialTheme.typography.titleMedium)
                        Text("MTU 1280 · MSS 1200 · keepalive 45s · compression off", style = MaterialTheme.typography.bodySmall)
                        Text("Public volunteer relays are not private or guaranteed stable.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (loading) "Updating relay list…" else "${countries.size} countries · ${servers.size} relays")
                    Button(onClick = { scope.launch { refresh() } }, enabled = !loading) { Text("Refresh") }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                selected?.let { server ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Selected relay: ${server.displayName}", style = MaterialTheme.typography.titleMedium)
                            Text("${server.pingMs} ms · ${server.speedBps / 1_000_000} Mbps · ${server.sessions} sessions")
                            Text("The native OpenVPN engine is not connected in this build.", style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(onClick = { selected = null }) { Text("Clear selection") }
                        }
                    }
                }
                if (loading && countries.isEmpty()) CircularProgressIndicator()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(countries, key = { it.code + it.country }) { summary ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("${summary.country} (${summary.code})", style = MaterialTheme.typography.titleMedium)
                                Text("${summary.servers} relays · best: ${summary.best.pingMs} ms · ${summary.best.speedBps / 1_000_000} Mbps")
                                Text("Log policy: ${summary.best.logPolicy}", style = MaterialTheme.typography.bodySmall)
                                Button(onClick = { selected = summary.best }) { Text("Select best relay") }
                            }
                        }
                    }
                }
            }
        }
    }
}
