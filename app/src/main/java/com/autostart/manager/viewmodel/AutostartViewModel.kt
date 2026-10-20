package com.autostart.manager.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autostart.manager.model.AutostartAppInfo
import com.autostart.manager.model.AutostartFilter
import com.autostart.manager.service.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AutostartUiState(
    val allApps: List<AutostartAppInfo> = emptyList(),
    val filter: AutostartFilter = AutostartFilter.ALL,
    val showSystemApps: Boolean = false,
    val isShizukuAvailable: Boolean = false,
    val hasShizukuPermission: Boolean = false,
    val isLoading: Boolean = true
) {
    val displayedApps: List<AutostartAppInfo>
        get() {
            val systemFiltered = if (showSystemApps) allApps else allApps.filter { !it.isSystemApp }
            return when (filter) {
                AutostartFilter.ALL -> systemFiltered
                AutostartFilter.ALLOWED -> systemFiltered.filter { it.isAllowed }
                AutostartFilter.NOT_ALLOWED -> systemFiltered.filter { !it.isAllowed }
            }
        }

    val totalCount: Int
        get() = if (showSystemApps) allApps.size else allApps.count { !it.isSystemApp }

    val allowedCount: Int
        get() {
            val list = if (showSystemApps) allApps else allApps.filter { !it.isSystemApp }
            return list.count { it.isAllowed }
        }

    val notAllowedCount: Int
        get() {
            val list = if (showSystemApps) allApps else allApps.filter { !it.isSystemApp }
            return list.count { !it.isAllowed }
        }
}

class AutostartViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AutostartUiState())
    val uiState: StateFlow<AutostartUiState> = _uiState.asStateFlow()

    init {
        checkShizukuState()
        refreshApps()
    }

    fun checkShizukuState() {
        val available = ShizukuManager.isShizukuAvailable()
        val hasPerm = ShizukuManager.hasPermission()
        _uiState.update { it.copy(isShizukuAvailable = available, hasShizukuPermission = hasPerm) }
    }

    fun refreshApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val context = getApplication<Application>()
            val apps = withContext(Dispatchers.IO) {
                loadBootCapableApps(context)
            }
            _uiState.update {
                it.copy(
                    allApps = apps,
                    isLoading = false
                )
            }
        }
    }

    fun setFilter(filter: AutostartFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun toggleShowSystemApps() {
        _uiState.update { it.copy(showSystemApps = !it.showSystemApps) }
    }

    fun toggleAppAutostart(app: AutostartAppInfo) {
        val targetAllowed = !app.isAllowed

        // Optimistic UI update
        _uiState.update { state ->
            val updated = state.allApps.map {
                if (it.packageName == app.packageName) it.copy(isAllowed = targetAllowed, isLoading = true)
                else it
            }
            state.copy(allApps = updated)
        }

        viewModelScope.launch {
            val success = ShizukuManager.setAutostartAllowed(
                packageName = app.packageName,
                bootReceivers = app.bootReceivers,
                allowed = targetAllowed
            )
            _uiState.update { state ->
                val updated = state.allApps.map {
                    if (it.packageName == app.packageName) {
                        it.copy(
                            isAllowed = if (success) targetAllowed else app.isAllowed,
                            isLoading = false
                        )
                    } else it
                }
                state.copy(allApps = updated)
            }
        }
    }

    /**
     * Strictly queries applications that declare boot startup capability.
     * Excludes static calculators, offline games, and apps with zero reboot startup ability.
     */
    private suspend fun loadBootCapableApps(context: Context): List<AutostartAppInfo> {
        val pm = context.packageManager
        val hasShizuku = ShizukuManager.hasPermission()
        val ignoredBootPackages = if (hasShizuku) ShizukuManager.getIgnoredBootPackages() else emptySet()

        // 1. Resolve all broadcast receivers listening to boot and startup intents
        val bootActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )

        val packageToReceivers = mutableMapOf<String, MutableList<String>>()

        for (action in bootActions) {
            try {
                val intent = Intent(action)
                val resolved = pm.queryBroadcastReceivers(
                    intent,
                    PackageManager.MATCH_DISABLED_COMPONENTS or PackageManager.GET_META_DATA
                )
                for (info in resolved) {
                    val actInfo = info.activityInfo ?: continue
                    val pkg = actInfo.packageName
                    val compName = "${actInfo.packageName}/${actInfo.name}"
                    val list = packageToReceivers.getOrPut(pkg) { mutableListOf() }
                    if (!list.contains(compName)) {
                        list.add(compName)
                    }
                }
            } catch (e: Throwable) {
                Log.e("AutostartViewModel", "Error resolving action $action", e)
            }
        }

        // 2. Query installed packages with permissions
        val packages = try {
            pm.getInstalledPackages(
                PackageManager.GET_PERMISSIONS or PackageManager.MATCH_DISABLED_COMPONENTS
            )
        } catch (e: Throwable) {
            emptyList()
        }

        val result = mutableListOf<AutostartAppInfo>()

        for (pkgInfo in packages) {
            val appInfo = pkgInfo.applicationInfo ?: continue
            val packageName = pkgInfo.packageName

            // Skip this app itself
            if (packageName == context.packageName) continue

            val receivers = packageToReceivers[packageName] ?: emptyList()
            val permissions = pkgInfo.requestedPermissions
            val hasBootPerm = permissions?.contains(android.Manifest.permission.RECEIVE_BOOT_COMPLETED) == true

            // STRICT FILTER: If an app has NO boot receivers AND NO boot permission, it CANNOT auto-start on reboot.
            if (receivers.isEmpty() && !hasBootPerm) {
                continue
            }

            val appName = try {
                appInfo.loadLabel(pm).toString()
            } catch (e: Throwable) {
                packageName
            }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            // Determine if boot autostart is currently allowed or disabled
            val isIgnoredInAppOps = ignoredBootPackages.contains(packageName)

            // Check if all declared boot receivers are disabled via package manager
            var areReceiversDisabled = false
            if (receivers.isNotEmpty()) {
                val allDisabled = receivers.all { compString ->
                    try {
                        val parts = compString.split("/")
                        if (parts.size == 2) {
                            val state = pm.getComponentEnabledSetting(ComponentName(parts[0], parts[1]))
                            state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        } else false
                    } catch (e: Throwable) {
                        false
                    }
                }
                areReceiversDisabled = allDisabled
            }

            val isAllowed = !isIgnoredInAppOps && !areReceiversDisabled

            result.add(
                AutostartAppInfo(
                    packageName = packageName,
                    appName = appName,
                    isSystemApp = isSystem,
                    isAllowed = isAllowed,
                    bootReceivers = receivers,
                    hasBootPermission = hasBootPerm
                )
            )
        }

        return result.sortedBy { it.appName.lowercase() }
    }
}
