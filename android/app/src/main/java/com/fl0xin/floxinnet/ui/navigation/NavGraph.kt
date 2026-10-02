package com.fl0xin.floxinnet.ui.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
data class Destination(val route:String,val label:String,val icon:ImageVector)
val bottomDestinations=listOf(Destination("home","Home",Icons.Default.Home),Destination("stats","Stats",Icons.Default.BarChart),Destination("codes","Codes",Icons.Default.Key),Destination("scenarios","Scenarios",Icons.Default.List),Destination("more","More",Icons.Default.MoreVert))
