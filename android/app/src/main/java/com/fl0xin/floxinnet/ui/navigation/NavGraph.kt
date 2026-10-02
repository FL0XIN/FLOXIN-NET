package com.fl0xin.floxinnet.ui.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.fl0xin.floxinnet.R
data class Destination(val route:String,val labelRes:Int,val icon:ImageVector)
val bottomDestinations=listOf(Destination("home",R.string.nav_home,Icons.Default.Home),Destination("stats",R.string.nav_stats,Icons.Default.BarChart),Destination("codes",R.string.nav_codes,Icons.Default.Key),Destination("scenarios",R.string.nav_scenarios,Icons.Default.List),Destination("more",R.string.nav_more,Icons.Default.MoreVert))
