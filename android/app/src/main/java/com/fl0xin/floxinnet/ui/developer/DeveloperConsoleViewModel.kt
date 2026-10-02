package com.fl0xin.floxinnet.ui.developer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fl0xin.floxinnet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeveloperConsoleViewModel(app: Application) : AndroidViewModel(app) {
    private val runner = DeveloperConsoleRunner(app)
    private val _lines = MutableStateFlow(listOf(app.getString(R.string.developer_title), app.getString(R.string.developer_hint), ""))
    val lines: StateFlow<List<String>> = _lines
    private val _fontSize = MutableStateFlow(13)
    val fontSize: StateFlow<Int> = _fontSize
    private val history = mutableListOf<String>()
    private var historyIndex = 0

    init { viewModelScope.launch(Dispatchers.IO) { append(runner.setup()) } }

    fun execute(command: String) {
        val value = command.trim()
        if (value.isEmpty()) return
        history += value
        historyIndex = history.size
        append("$ $value")
        viewModelScope.launch(Dispatchers.IO) {
            val result = runner.execute(value)
            if (result == "__CLEAR__") _lines.value = emptyList() else append(result)
        }
    }

    fun previous(): String {
        historyIndex = (historyIndex - 1).coerceAtLeast(0)
        return history.getOrNull(historyIndex).orEmpty()
    }
    fun next(): String = history.getOrNull((historyIndex + 1).coerceAtMost(history.size)).also { historyIndex = (historyIndex + 1).coerceAtMost(history.size) } ?: ""
    fun increaseFont() { _fontSize.value = (_fontSize.value + 1).coerceAtMost(20) }
    fun decreaseFont() { _fontSize.value = (_fontSize.value - 1).coerceAtLeast(10) }
    fun allText(): String = _lines.value.joinToString("\n")
    private fun append(value: String) { _lines.value = (_lines.value + value.split('\n')).takeLast(1000) }
}
