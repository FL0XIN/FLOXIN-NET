package com.fl0xin.floxinnet.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.data.LanguageStore
import com.fl0xin.floxinnet.ui.components.*

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val provider by vm.provider.collectAsState(); val logging by vm.logging.collectAsState(); val mode by vm.mode.collectAsState(); val language by vm.language.collectAsState()
    Page(stringResource(R.string.settings_title)) {
        PremiumCard(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.mode), style = MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ModeChoice(stringResource(R.string.booster), mode == "booster") { vm.saveMode("booster") }
                ModeChoice(stringResource(R.string.saver), mode == "saver") { vm.saveMode("saver") }
                ModeChoice(stringResource(R.string.two_x_one), mode == "2x1") { vm.saveMode("2x1") }
            }
            Text(stringResource(R.string.network)); Text(stringResource(R.string.theme)); Text(stringResource(R.string.cache_ttl))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(stringResource(R.string.logging)); Switch(logging, vm::saveLogging) }
            Text(stringResource(R.string.current, provider))
        }
        PremiumCard(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleLarge)
            LazyColumn(Modifier.heightIn(max = 260.dp)) {
                items(LanguageStore.languages, key = { it }) { tag -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(languageLabel(tag)); RadioButton(tag == language, { vm.saveLanguage(tag) }) } }
            }
        }
    }
}

@Composable private fun ModeChoice(label: String, selected: Boolean, onClick: () -> Unit) { Row { RadioButton(selected, onClick); Text(label) } }

@Composable private fun languageLabel(tag: String): String = when (tag) {
    "auto" -> stringResource(R.string.language_auto); "en" -> stringResource(R.string.language_english); "ar" -> stringResource(R.string.language_arabic); "zh" -> stringResource(R.string.language_chinese); "ru" -> stringResource(R.string.language_russian); "ja" -> stringResource(R.string.language_japanese); "ko" -> stringResource(R.string.language_korean); "fr" -> stringResource(R.string.language_french); "de" -> stringResource(R.string.language_german); "es", "es-MX", "es-AR" -> stringResource(R.string.language_spanish); "it" -> stringResource(R.string.language_italian); "pt", "pt-BR" -> stringResource(R.string.language_portuguese); "nl" -> stringResource(R.string.language_dutch); "pl" -> stringResource(R.string.language_polish); "tr" -> stringResource(R.string.language_turkish); "hi" -> stringResource(R.string.language_hindi); "th" -> stringResource(R.string.language_thai); "vi" -> stringResource(R.string.language_vietnamese); "id" -> stringResource(R.string.language_indonesian); else -> tag
}
