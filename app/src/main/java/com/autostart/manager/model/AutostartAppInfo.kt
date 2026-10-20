package com.autostart.manager.model

data class AutostartAppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val isAllowed: Boolean,
    val hasBootPermission: Boolean = false,
    val isLoading: Boolean = false
)
