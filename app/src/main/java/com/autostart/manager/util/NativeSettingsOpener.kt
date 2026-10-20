package com.autostart.manager.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.autostart.manager.service.ShizukuManager

object NativeSettingsOpener {
    private const val TAG = "NativeSettingsOpener"

    // Exhaustive registry of OEM autostart / auto-launch activities
    private val OEM_COMPONENTS = listOf(
        // Realme UI / ColorOS / Oppo / OnePlus (OPlus OS)
        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
        ComponentName("com.oplus.safecenter", "com.oplus.safecenter.permission.startup.StartupAppListActivity"),
        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),
        ComponentName("com.oplus.battery", "com.oplus.battery.settings.AppBatteryManagerActivity"),
        ComponentName("com.oplus.battery", "com.oplus.battery.AppListActivity"),
        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity"),
        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"),
        ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"),

        // Xiaomi / MIUI / HyperOS
        ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
        ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartAppListActivity"),

        // Huawei / Honor (EMUI / MagicOS)
        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"),
        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity"),

        // Vivo / iQOO (FuntouchOS / OriginOS)
        ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
        ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"),
        ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"),

        // Samsung (One UI)
        ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity"),
        ComponentName("com.samsung.android.sm", "com.samsung.android.sm.battery.ui.BatteryActivity"),
        ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.cstyle.BatteryActivity"),

        // Transsion (Tecno / Infinix HiOS / XOS)
        ComponentName("com.transsion.phonemanager", "com.transsion.phonemanager.settings.AutoStartAppsActivity"),

        // ASUS
        ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity"),
        ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.powersaver.PowerSaverSettings")
    )

    /**
     * Tries to open the native deep settings page for autostart.
     * Shows a toast "Native Settings Not Found" if no native screen can be launched.
     */
    fun open(context: Context): Boolean {
        val pm = context.packageManager

        // 1. First attempt: standard unprivileged launch
        for (component in OEM_COMPONENTS) {
            try {
                val intent = Intent().apply {
                    this.component = component
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val resolved = pm.queryIntentActivities(intent, 0)
                if (resolved.isNotEmpty()) {
                    context.startActivity(intent)
                    Log.i(TAG, "Launched native autostart: ${component.flattenToShortString()}")
                    return true
                }
            } catch (e: SecurityException) {
                Log.w(TAG, "Standard launch restricted for ${component.flattenToShortString()}", e)
            } catch (e: Throwable) {
                Log.d(TAG, "Could not launch ${component.flattenToShortString()}: ${e.message}")
            }
        }

        // 2. Second attempt: Elevated ADB launch via Shizuku if active
        if (ShizukuManager.hasPermission()) {
            for (component in OEM_COMPONENTS) {
                try {
                    val compString = component.flattenToShortString()
                    val output = ShizukuManager.execCommand(
                        arrayOf("am", "start", "-n", compString)
                    )
                    if (output.contains("Starting: Intent") && !output.contains("Error") && !output.contains("Exception")) {
                        Log.i(TAG, "Privileged Shizuku launch succeeded: $compString")
                        return true
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "Privileged launch error", e)
                }
            }
        }

        // Fallback: Notify user that native OEM autostart settings are not found on this OS
        Toast.makeText(context, "Native Settings Not Found", Toast.LENGTH_SHORT).show()
        return false
    }
}
