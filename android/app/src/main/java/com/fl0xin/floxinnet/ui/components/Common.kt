package com.fl0xin.floxinnet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fl0xin.floxinnet.ui.theme.CyberBlue

@Composable
fun Page(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = CyberBlue)
        content()
    }
}

@Composable
fun RowScope.StatTile(label: String, value: String) {
    Card(Modifier.weight(1f)) { Column(Modifier.padding(10.dp)) { Text(label); Text(value, style = MaterialTheme.typography.titleLarge, color = CyberBlue) } }
}
