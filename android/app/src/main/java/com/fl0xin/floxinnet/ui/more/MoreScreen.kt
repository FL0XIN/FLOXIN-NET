package com.fl0xin.floxinnet.ui.more
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fl0xin.floxinnet.ui.components.*
@Composable fun MoreScreen(nav:NavHostController){Page("More"){listOf("providers" to "Providers","settings" to "Settings","logs" to "Logs","doctor" to "Doctor","env" to "Environment","blocklist" to "Blocklist","about" to "About").forEach{(route,label)->Button(onClick={nav.navigate(route)},modifier=Modifier.fillMaxWidth()){Text(label)}}}}
