package com.fl0xin.floxinnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fl0xin.floxinnet.data.*
import com.fl0xin.floxinnet.di.TermuxBridge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp class FloxinApplication : android.app.Application()
@AndroidEntryPoint class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { FloxinTheme { FloxinApp() } } } }

private val CyberBlue = Color(0xFF00D9FF); private val DarkBg = Color(0xFF0A0E1A)
@Composable fun FloxinTheme(content: @Composable () -> Unit) { MaterialTheme(colorScheme = darkColorScheme(primary = CyberBlue, background = DarkBg, surface = Color(0xFF111827)), content = content) }
@Composable fun FloxinApp() { var screen by rememberSaveable { mutableStateOf("home") }; Scaffold(bottomBar = { NavigationBar { NavigationBarItem(screen == "home", { screen = "home" }, { Text("Home") }); NavigationBarItem(screen == "stats", { screen = "stats" }, { Text("Stats") }); NavigationBarItem(screen == "scenarios", { screen = "scenarios" }, { Text("Scenarios") }); NavigationBarItem(screen == "settings", { screen = "settings" }, { Text("Settings") }) } }) { p -> Box(Modifier.padding(p)) { when (screen) { "home" -> HomeScreen(); "stats" -> StatsScreen(); "scenarios" -> ScenariosScreen(); else -> SettingsScreen() } } } }

@HiltViewModel class HomeViewModel @Inject constructor(private val api: FloxinApi, private val termux: TermuxBridge) : ViewModel() { val status = MutableStateFlow<Status?>(null); val stats = MutableStateFlow<Stats?>(null); val message = MutableStateFlow(""); init { refresh() }; fun refresh() = viewModelScope.launch { runCatching { status.value = api.status(); stats.value = api.stats() }.onFailure { message.value = termux.setupMessage() } }; fun toggle() = viewModelScope.launch { runCatching { if (status.value?.running == true) api.stop() else api.start(); refresh() }.onFailure { message.value = termux.setupMessage() } } }
@Composable fun HomeScreen(vm: HomeViewModel = hiltViewModel()) { val status by vm.status.collectAsState(); val stats by vm.stats.collectAsState(); val message by vm.message.collectAsState(); LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { item { Text("FLOXIN NET", style = MaterialTheme.typography.headlineMedium, color = CyberBlue); Text(if (status?.running == true) "DNS running" else "DNS stopped", color = if (status?.running == true) CyberBlue else Color.LightGray) }; item { Button(onClick = vm::toggle, modifier = Modifier.fillMaxWidth()) { Text(if (status?.running == true) "Stop DNS" else "Start DNS") } }; item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatCard("Blocked", stats?.blocked ?: 0); StatCard("Cached", stats?.cached ?: 0); StatCard("Forwarded", stats?.forwarded ?: 0) } }; if (message.isNotBlank()) item { Text(message, color = MaterialTheme.colorScheme.error) } } }
@Composable fun RowScope.StatCard(label: String, value: Int) { Card(Modifier.weight(1f)) { Column(Modifier.padding(10.dp)) { Text(label); Text(value.toString(), style = MaterialTheme.typography.titleLarge, color = CyberBlue) } } }

@HiltViewModel class StatsViewModel @Inject constructor(private val api: FloxinApi) : ViewModel() { val stats = MutableStateFlow<Stats?>(null); init { viewModelScope.launch { runCatching { stats.value = api.stats() } } } }
@Composable fun StatsScreen(vm: StatsViewModel = hiltViewModel()) { val stats by vm.stats.collectAsState(); LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { item { Text("Statistics", style = MaterialTheme.typography.headlineMedium, color = CyberBlue) }; item { Text("Last 24 hours", style = MaterialTheme.typography.titleMedium) }; item { stats?.let { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatCard("Queries", it.total_queries); StatCard("Blocked", it.blocked) } } ?: Text("Loading…") }; item { stats?.let { LinearProgressIndicator(progress = { if (it.total_queries == 0) 0f else it.blocked.toFloat() / it.total_queries }, modifier = Modifier.fillMaxWidth()) } }; item { Text("SQLite-backed counters are ready; hourly charts will use the timestamped query table in the next polish pass.", color = Color.LightGray) } } }

@HiltViewModel class ScenariosViewModel @Inject constructor(private val api: FloxinApi) : ViewModel() { val scenarios = MutableStateFlow<List<Scenario>>(emptyList()); val applied = MutableStateFlow(""); init { viewModelScope.launch { runCatching { scenarios.value = api.scenarios() } } }; fun apply(item: Scenario) = viewModelScope.launch { runCatching { api.applyScenario(ScenarioRequest(item.code)); applied.value = "Applied ${item.code}" }.onFailure { applied.value = "Could not apply ${item.code}" } } }
@Composable fun ScenariosScreen(vm: ScenariosViewModel = hiltViewModel()) { val scenarios by vm.scenarios.collectAsState(); val applied by vm.applied.collectAsState(); LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Scenarios", style = MaterialTheme.typography.headlineMedium, color = CyberBlue) }; if (applied.isNotBlank()) item { Text(applied, color = CyberBlue) }; items(scenarios) { item -> Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text(item.code); Text("${item.type} · ${item.description}", color = Color.LightGray) }; TextButton(onClick = { vm.apply(item) }) { Text("Apply") } } } } } }

@HiltViewModel class SettingsViewModel @Inject constructor(private val termux: TermuxBridge) : ViewModel() { var provider by mutableStateOf("Cloudflare"); var network by mutableStateOf("AUTO"); var dark by mutableStateOf(true); val termuxInstalled get() = termux.isTermuxInstalled(); val apiInstalled get() = termux.isTermuxApiInstalled() }
@Composable fun SettingsScreen(vm: SettingsViewModel = hiltViewModel()) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("Settings", style = MaterialTheme.typography.headlineMedium, color = CyberBlue) }; item { OutlinedTextField(vm.provider, { vm.provider = it }, label = { Text("DNS Provider") }, modifier = Modifier.fillMaxWidth()) }; item { OutlinedTextField(vm.network, { vm.network = it }, label = { Text("Network: WiFi / SIM / Both") }, modifier = Modifier.fillMaxWidth()) }; item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Dark theme"); Switch(vm.dark, { vm.dark = it }) } }; item { Text(if (vm.termuxInstalled) "Termux detected" else "Termux is not installed. Install it from F-Droid.") }; item { Text(if (vm.apiInstalled) "Termux:API detected" else "Termux:API optional; install it for automation.") } } }
