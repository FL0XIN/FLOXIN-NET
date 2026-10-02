package com.fl0xin.floxinnet
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import com.fl0xin.floxinnet.ui.about.AboutScreen
import com.fl0xin.floxinnet.ui.blocklist.BlocklistScreen
import com.fl0xin.floxinnet.ui.codes.CodesScreen
import com.fl0xin.floxinnet.ui.doctor.DoctorScreen
import com.fl0xin.floxinnet.ui.developer.DeveloperConsoleScreen
import com.fl0xin.floxinnet.ui.env.EnvScreen
import com.fl0xin.floxinnet.ui.home.HomeScreen
import com.fl0xin.floxinnet.ui.logs.LogsScreen
import com.fl0xin.floxinnet.ui.more.MoreScreen
import com.fl0xin.floxinnet.ui.providers.ProvidersScreen
import com.fl0xin.floxinnet.ui.scenarios.ScenariosScreen
import com.fl0xin.floxinnet.ui.settings.SettingsScreen
import com.fl0xin.floxinnet.ui.stats.StatsScreen
import com.fl0xin.floxinnet.ui.theme.FloxinTheme
import com.fl0xin.floxinnet.ui.navigation.bottomDestinations
import com.fl0xin.floxinnet.data.LanguageStore
import kotlinx.coroutines.runBlocking
@HiltAndroidApp class FloxinApplication:android.app.Application()
@AndroidEntryPoint class MainActivity:ComponentActivity(){override fun onCreate(state:Bundle?){LanguageStore.apply(runBlocking{LanguageStore.current(this@MainActivity)});installSplashScreen();super.onCreate(state);setContent{FloxinTheme{FloxinRoot()}}}}
@Composable fun FloxinRoot(){val nav=rememberNavController();Scaffold(bottomBar={NavigationBar{bottomDestinations.forEach{d->NavigationBarItem(selected=nav.currentDestination?.route==d.route,onClick={nav.navigate(d.route)},icon={Icon(d.icon,stringResource(d.labelRes))},label={Text(stringResource(d.labelRes))})}}}){p->NavHost(nav,"home",Modifier.padding(p)){composable("home"){HomeScreen()};composable("stats"){StatsScreen()};composable("codes"){CodesScreen()};composable("scenarios"){ScenariosScreen()};composable("more"){MoreScreen(nav)};composable("providers"){ProvidersScreen()};composable("settings"){SettingsScreen()};composable("logs"){LogsScreen()};composable("doctor"){DoctorScreen()};composable("env"){EnvScreen()};composable("blocklist"){BlocklistScreen()};composable("about"){AboutScreen(nav)};composable("developer"){DeveloperConsoleScreen()}}}}
