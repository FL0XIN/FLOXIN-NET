package com.fl0xin.floxinnet.ui.more
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.Page
@Composable fun MoreScreen(nav:NavHostController){val entries=listOf("providers" to R.string.providers,"settings" to R.string.settings,"logs" to R.string.logs,"doctor" to R.string.doctor,"env" to R.string.environment,"blocklist" to R.string.blocklist,"about" to R.string.about);Page(stringResource(R.string.more_title)){entries.forEach{(route,label)->Button(onClick={nav.navigate(route)},modifier=Modifier.fillMaxWidth()){Text(stringResource(label))}}}}
