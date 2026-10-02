package com.fl0xin.floxinnet.ui.developer

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class DeveloperConsoleRunner(private val context: Context) {
    private val home = File(context.filesDir, "home")
    private val repo = File(home, "floxin-net")
    private val marker = File(repo, ".setup-complete")

    fun setup(): String {
        home.mkdirs()
        if (marker.exists()) return "[setup] FLOXIN-NET is already installed in $repo"
        return try {
            val zip = File(home, "floxin-net.zip")
            val connection = URL("https://github.com/FL0XIN/FLOXIN-NET/archive/refs/heads/main.zip").openConnection() as HttpURLConnection
            connection.connectTimeout = 15_000
            connection.readTimeout = 60_000
            connection.inputStream.use { input -> zip.outputStream().use { output -> input.copyTo(output) } }
            ZipInputStream(zip.inputStream().buffered()).use { stream ->
                var entry = stream.nextEntry
                while (entry != null) {
                    val relative = entry.name.substringAfter('/', "")
                    if (relative.isNotEmpty()) {
                        val target = File(repo, relative)
                        if (entry.isDirectory) target.mkdirs() else { target.parentFile?.mkdirs(); target.outputStream().use { stream.copyTo(it) } }
                    }
                    entry = stream.nextEntry
                }
            }
            zip.delete()
            val install = File(repo, "install.sh")
            val installOutput = if (install.exists()) runProcess("sh ${shellQuote(install.absolutePath)}", repo) else "install.sh not found"
            marker.writeText("installed")
            "[setup] Downloaded repository to $repo\n$installOutput\n[setup] HOME=$home"
        } catch (error: Exception) {
            "[setup:error] ${error.message ?: error.javaClass.simpleName}"
        }
    }

    fun execute(command: String): String {
        val trimmed = command.trim()
        if (trimmed.isEmpty()) return ""
        return when (trimmed.lowercase()) {
            "help" -> "Commands: setup, FLOXIN, DNSF, TESTALL, SOUSG, status, clear, history"
            "setup" -> setup()
            "status" -> "HOME=$home\nREPOSITORY=${if (repo.exists()) repo else "not installed"}\nFLOXIN=${findExecutable("FLOXIN") ?: "not found"}"
            "clear" -> "__CLEAR__"
            "history" -> "History is managed by the console UI"
            else -> {
                val executable = findExecutable(trimmed.substringBefore(' '))
                if (executable == null && trimmed.substringBefore(' ').uppercase() in setOf("FLOXIN", "DNSF", "TESTALL", "SOUSG")) {
                    "[error] Command not installed. Run: setup"
                } else runProcess(if (executable != null) executable + trimmed.removePrefix(trimmed.substringBefore(' ')) else trimmed, repo)
            }
        }
    }

    private fun findExecutable(name: String): String? {
        val candidates = listOf(File(home, "bin/$name"), File(repo, "bin/$name"), File(repo, name))
        return candidates.firstOrNull { it.isFile && it.canRead() }?.absolutePath
    }

    private fun runProcess(command: String, directory: File): String = try {
        val process = ProcessBuilder("/system/bin/sh", "-c", command).directory(directory).redirectErrorStream(true).apply {
            environment()["HOME"] = home.absolutePath
            environment()["PATH"] = "${File(home, "bin").absolutePath}:/system/bin:/system/xbin"
        }.start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        output.ifBlank { "[exit ${process.exitValue()}]" }.takeLast(20_000)
    } catch (error: Exception) { "[exec:error] ${error.message ?: error.javaClass.simpleName}" }

    private fun shellQuote(value: String) = "'" + value.replace("'", "'\\''") + "'"
}
