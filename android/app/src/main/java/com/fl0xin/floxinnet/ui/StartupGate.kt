package com.fl0xin.floxinnet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StartupGate(content: @Composable () -> Unit) {
    var ready by remember { mutableStateOf(false) }
    var skipped by remember { mutableStateOf(false) }
    var elapsed by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (elapsed < 30 && !skipped) { delay(1000); elapsed++ }
        ready = true
    }
    if (ready || skipped) content() else {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF0A0E1A)) {
            Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
                Text("FLOXIN NET", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF00D9FF))
                Spacer(Modifier.height(12.dp))
                Text("Preparing DNS protection and local data", color = Color.LightGray)
                Text("This startup check may take up to 30 seconds.", color = Color.Gray)
                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(progress = { elapsed / 30f }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("${30 - elapsed}s remaining", color = Color.Gray)
                Spacer(Modifier.height(18.dp))
                OutlinedButton(onClick = { skipped = true }) { Text("Skip") }
            }
        }
    }
}
