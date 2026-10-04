package com.fl0xin.floxinnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import com.fl0xin.floxinnet.data.LanguageStore
import com.fl0xin.floxinnet.ui.about.AboutScreen
import com.fl0xin.floxinnet.ui.blocklist.BlocklistScreen
import com.fl0xin.floxinnet.ui.codes.CodesScreen
import com.fl0xin.floxinnet.ui.developer.DeveloperConsoleScreen
import com.fl0xin.floxinnet.ui.doctor.DoctorScreen
import com.fl0xin.floxinnet.ui.env.EnvScreen
import com.fl0xin.floxinnet.ui.home.HomeScreen
import com.fl0xin.floxinnet.ui.logs.LogsScreen
import com.fl0xin.floxinnet.ui.more.MoreScreen
import com.fl0xin.floxinnet.ui.navigation.bottomDestinations
import com.fl0xin.floxinnet.ui.providers.ProvidersScreen
import com.fl0xin.floxinnet.ui.scenarios.ScenariosScreen
import com.fl0xin.floxinnet.ui.settings.SettingsScreen
import com.fl0xin.floxinnet.ui.stats.StatsScreen
import com.fl0xin.floxinnet.ui.StartupGate
import com.fl0xin.floxinnet.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltAndroidApp class FloxinApplication : android.app.Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) { installSplashScreen(); super.onCreate(state); setContent { FloxinTheme { StartupGate { FloxinRoot() } } }; lifecycleScope.launch(Dispatchers.IO) { val saved = LanguageStore.current(this@MainActivity); withContext(Dispatchers.Main) { LanguageStore.apply(saved) } } }
}

@Composable
fun FloxinRoot() {
    val nav = rememberNavController(); val entry = nav.currentBackStackEntryAsState().value
    Column(Modifier.fillMaxSize().background(DarkBg)) {
        Box(Modifier.weight(1f)) { NavHost(nav, "home", Modifier.fillMaxSize()) { composable("home") { HomeScreen() }; composable("stats") { StatsScreen() }; composable("codes") { CodesScreen() }; composable("scenarios") { ScenariosScreen() }; composable("more") { MoreScreen(nav) }; composable("providers") { ProvidersScreen() }; composable("settings") { SettingsScreen() }; composable("logs") { LogsScreen() }; composable("doctor") { DoctorScreen() }; composable("env") { EnvScreen() }; composable("blocklist") { BlocklistScreen() }; composable("about") { AboutScreen(nav) }; composable("developer") { DeveloperConsoleScreen() } } }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).clip(RoundedCornerShape(24.dp)).background(Surface).padding(6.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            bottomDestinations.forEach { destination ->
                val selected = entry?.destination?.route == destination.route
                val tint by animateColorAsState(if (selected) CyberBlue else TextMuted, label = "navTint")
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(if (selected) Color(0x2625D9FF) else Color.Transparent).clickable { if (!selected) nav.navigate(destination.route) }, contentAlignment = Alignment.Center) { Icon(destination.icon, stringResource(destination.labelRes), tint = tint) }
            }
        }
    }
}
