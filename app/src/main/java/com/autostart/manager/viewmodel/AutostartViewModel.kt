package com.autostart.manager.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.PowerManager
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
                loadInstalledApps(context)
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
            val success = ShizukuManager.setAutostartAllowed(app.packageName, targetAllowed)
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

    private suspend fun loadInstalledApps(context: Context): List<AutostartAppInfo> {
        val pm = context.packageManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val hasShizuku = ShizukuManager.hasPermission()
        val restrictedPackages = if (hasShizuku) ShizukuManager.getRestrictedPackages() else emptySet()

        val packages = try {
            pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        } catch (e: Throwable) {
            emptyList()
        }

        val result = mutableListOf<AutostartAppInfo>()

        for (pkgInfo in packages) {
            val appInfo = pkgInfo.applicationInfo ?: continue
            val packageName = pkgInfo.packageName

            // Skip this app itself
            if (packageName == context.packageName) continue

            val appName = try {
                appInfo.loadLabel(pm).toString()
            } catch (e: Throwable) {
                packageName
            }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            // Check if app declared RECEIVE_BOOT_COMPLETED permission
            val permissions = pkgInfo.requestedPermissions
            val hasBootPerm = permissions?.contains(android.Manifest.permission.RECEIVE_BOOT_COMPLETED) == true

            // Determine if allowed
            val isAllowed = if (hasShizuku) {
                !restrictedPackages.contains(packageName)
            } else {
                // Fallback: check battery optimization whitelist or boot permission
                powerManager?.isIgnoringBatteryOptimizations(packageName) == true || hasBootPerm
            }

            result.add(
                AutostartAppInfo(
                    packageName = packageName,
                    appName = appName,
                    isSystemApp = isSystem,
                    isAllowed = isAllowed,
                    hasBootPermission = hasBootPerm
                )
            )
        }

        // Sort alphabetically by app name
        return result.sortedBy { it.appName.lowercase() }
    }
}
