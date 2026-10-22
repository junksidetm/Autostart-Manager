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
     * Queries all packages where BOOT_COMPLETED is ignored (blocked from starting on boot).
     */
    suspend fun getIgnoredBootPackages(): Set<String> = withContext(Dispatchers.IO) {
        if (!hasPermission()) return@withContext emptySet()

        val bootOutput = execCommand(
            arrayOf("cmd", "appops", "query-op", "--user", "0", "BOOT_COMPLETED", "ignore")
        )
        AutostartParser.parsePackageList(bootOutput)
    }

    /**
     * Queries disabled receiver components across the system via elevated Shizuku shell.
     */
    suspend fun getDisabledReceiverComponents(): Set<String> = withContext(Dispatchers.IO) {
        val disabled = mutableSetOf<String>()
        if (!hasPermission()) return@withContext disabled

        val actions = listOf(
            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.LOCKED_BOOT_COMPLETED",
            "android.intent.action.QUICKBOOT_POWERON"
        )
        for (action in actions) {
            val output = execCommand(arrayOf("pm", "query-receivers", "-d", "--components", "-a", action))
            disabled.addAll(AutostartParser.parseComponents(output))
        }
        disabled
    }

    /**
     * Toggles reboot autostart status for a package.
     * When allowed = false:
     *   1. Sets BOOT_COMPLETED AppOp to ignore (system drops boot broadcast).
     *   2. Disables the package's boot broadcast receivers via 'pm disable'.
     * When allowed = true:
     *   1. Sets BOOT_COMPLETED AppOp to allow.
     *   2. Re-enables the package's boot broadcast receivers via 'pm enable'.
     * 
     * Uses defensive regex sanitization and transactional single-shell execution.
     */
    suspend fun setAutostartAllowed(
        packageName: String,
        bootReceivers: List<String>,
        allowed: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission()) return@withContext false
        if (!AutostartParser.isPackageName(packageName)) {
            Log.e(TAG, "Rejected invalid package name: $packageName")
            return@withContext false
        }

        try {
            val validReceivers = bootReceivers.mapNotNull { AutostartParser.normalize(it) }
            val batchCommand = AutostartParser.switchCommand(packageName, validReceivers, allowed)
            execCommand(arrayOf("sh", "-c", "exec 2>&1; $batchCommand"))
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Failed toggling reboot autostart for $packageName", e)
            false
        }
    }
}
