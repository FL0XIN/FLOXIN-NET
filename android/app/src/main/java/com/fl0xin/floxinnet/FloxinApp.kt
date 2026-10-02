package com.fl0xin.floxinnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.fl0xin.floxinnet.ui.theme.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltAndroidApp
class FloxinApplication : android.app.Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) {
        installSplashScreen()
        super.onCreate(state)
        setContent { FloxinTheme { FloxinRoot() } }
        lifecycleScope.launch(Dispatchers.IO) {
            val saved = LanguageStore.current(this@MainActivity)
            withContext(Dispatchers.Main) { LanguageStore.apply(saved) }
        }
    }
}

@Composable
fun FloxinRoot() {
    val nav = rememberNavController()
    val entry = nav.currentBackStackEntryAsState().value
    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(containerColor = DarkBg, tonalElevation = 0.dp) {
                bottomDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = entry?.destination?.route == destination.route,
                        onClick = { if (entry?.destination?.route != destination.route) nav.navigate(destination.route) },
                        icon = { Icon(destination.icon, stringResource(destination.labelRes)) },
                        label = { Text(stringResource(destination.labelRes), style = MaterialTheme.typography.labelMedium) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CyberBlue, selectedTextColor = CyberBlue, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = Surface2)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(nav, "home", Modifier.padding(padding)) {
            composable("home") { HomeScreen() }
            composable("stats") { StatsScreen() }
            composable("codes") { CodesScreen() }
            composable("scenarios") { ScenariosScreen() }
            composable("more") { MoreScreen(nav) }
            composable("providers") { ProvidersScreen() }
            composable("settings") { SettingsScreen() }
            composable("logs") { LogsScreen() }
            composable("doctor") { DoctorScreen() }
            composable("env") { EnvScreen() }
            composable("blocklist") { BlocklistScreen() }
            composable("about") { AboutScreen(nav) }
            composable("developer") { DeveloperConsoleScreen() }
        }
    }
}
