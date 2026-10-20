package com.autostart.manager.service

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

object ShizukuManager {
    private const val TAG = "ShizukuManager"

    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Throwable) {
            false
        }
    }

    fun hasPermission(): Boolean {
        return try {
            if (!isShizukuAvailable()) return false
            if (Shizuku.isPreV11()) return false
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Throwable) {
            false
        }
    }

    fun requestPermission(requestCode: Int = 2001) {
        try {
            if (isShizukuAvailable() && !Shizuku.isPreV11()) {
                Shizuku.requestPermission(requestCode)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed requesting Shizuku permission", e)
        }
    }

    /**
     * Executes an ADB shell command via Shizuku reflection to support all Android versions.
     */
    fun execCommand(command: Array<String>): String {
        return try {
            val clazz = Class.forName("rikka.shizuku.Shizuku")
            val method = clazz.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            method.isAccessible = true
            val process = method.invoke(null, command, null, null) as Process
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            process.destroy()
            output.trim()
        } catch (e: Throwable) {
            Log.e(TAG, "Failed command: ${command.joinToString(" ")}", e)
            ""
        }
    }

    /**
     * Queries all packages where BOOT_COMPLETED, RUN_IN_BACKGROUND, or RUN_ANY_IN_BACKGROUND is ignored.
     */
    suspend fun getRestrictedPackages(): Set<String> = withContext(Dispatchers.IO) {
        val restricted = mutableSetOf<String>()
        if (!hasPermission()) return@withContext restricted

        // 1. Query ignored BOOT_COMPLETED
        val bootOutput = execCommand(
            arrayOf("cmd", "appops", "query-op", "--user", "0", "BOOT_COMPLETED", "ignore")
        )
        parseQueryOpOutput(bootOutput, restricted)

        // 2. Query ignored RUN_IN_BACKGROUND
        val runOutput = execCommand(
            arrayOf("cmd", "appops", "query-op", "--user", "0", "RUN_IN_BACKGROUND", "ignore")
        )
        parseQueryOpOutput(runOutput, restricted)

        // 3. Query ignored RUN_ANY_IN_BACKGROUND
        val runAnyOutput = execCommand(
            arrayOf("cmd", "appops", "query-op", "--user", "0", "RUN_ANY_IN_BACKGROUND", "ignore")
        )
        parseQueryOpOutput(runAnyOutput, restricted)

        restricted
    }

    private fun parseQueryOpOutput(output: String, targetSet: MutableSet<String>) {
        output.lineSequence().forEach { line ->
            val trimmed = line.trim()
            val pkg = trimmed.substringBefore(":").substringBefore(" ").trim()
            if (pkg.isNotEmpty() && pkg.contains(".")) {
                targetSet.add(pkg)
            }
        }
    }

    /**
     * Toggles autostart status for a package.
     * When allowed = true: sets BOOT_COMPLETED, RUN_IN_BACKGROUND, and RUN_ANY_IN_BACKGROUND to allow.
     * When allowed = false: sets BOOT_COMPLETED, RUN_IN_BACKGROUND, and RUN_ANY_IN_BACKGROUND to ignore.
     */
    suspend fun setAutostartAllowed(packageName: String, allowed: Boolean): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission()) return@withContext false

        val mode = if (allowed) "allow" else "ignore"
        try {
            execCommand(arrayOf("cmd", "appops", "set", packageName, "BOOT_COMPLETED", mode))
            execCommand(arrayOf("cmd", "appops", "set", packageName, "RUN_IN_BACKGROUND", mode))
            execCommand(arrayOf("cmd", "appops", "set", packageName, "RUN_ANY_IN_BACKGROUND", mode))

            if (allowed) {
                // If allowing, also ensure deviceidle battery exemption is added
                execCommand(arrayOf("dumpsys", "deviceidle", "whitelist", "+$packageName"))
            }
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Failed toggling autostart for $packageName", e)
            false
        }
    }
}
