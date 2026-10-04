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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

@androidx.compose.runtime.Composable
private fun FloxinVpnApp() {
    val catalog = remember { VpnGateCatalog() }
    val scope = rememberCoroutineScope()
    var servers by remember { mutableStateOf<List<VpnServer>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun refresh() {
        loading = true
        error = null
        runCatching { withContext(Dispatchers.IO) { catalog.fetch() } }
            .onSuccess { servers = VpnRanking.rank(it) }
            .onFailure { error = it.message ?: "Could not load public servers" }
        loading = false
    }
    LaunchedEffect(Unit) { refresh() }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = androidx.compose.ui.graphics.Color(0xFF070B14)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("FLOXIN VPN", style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color(0xFF00D9FF))
                Text("Free public relays · measured for speed · limited-data mode", color = androidx.compose.ui.graphics.Color.LightGray)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Data-saving profile", style = MaterialTheme.typography.titleMedium)
                        Text("MTU 1280 · MSS 1200 · keepalive 45s · compression off", style = MaterialTheme.typography.bodySmall)
                        Text("Public relays are volunteer-operated; do not use them for sensitive accounts.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (loading) "Updating relay list…" else "${servers.size} measured candidates")
                    Button(onClick = { scope.launch { refresh() } }, enabled = !loading) { Text("Refresh") }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                if (loading && servers.isEmpty()) CircularProgressIndicator()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(servers, key = { "${it.hostName}-${it.ip}" }) { server ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(server.displayName, style = MaterialTheme.typography.titleMedium)
                                Text("${server.pingMs} ms · ${server.speedBps / 1_000_000} Mbps · ${server.sessions} sessions")
                                Text("Log policy: ${server.logPolicy}", style = MaterialTheme.typography.bodySmall)
                                Button(onClick = { /* OpenVPN 3 native bridge connects here in Phase 2 */ }) { Text("Select") }
                            }
                        }
                    }
                }
            }
        }
    }
}
