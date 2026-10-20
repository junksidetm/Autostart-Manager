package com.autostart.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autostart.manager.service.ShizukuManager
import com.autostart.manager.ui.screens.HomeScreen
import com.autostart.manager.ui.theme.AutostartManagerTheme
import com.autostart.manager.viewmodel.AutostartViewModel
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {

    private val viewModel: AutostartViewModel by viewModels()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        viewModel.checkShizukuState()
        viewModel.refreshApps()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        viewModel.checkShizukuState()
    }

    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, _ ->
        viewModel.checkShizukuState()
        viewModel.refreshApps()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            // Ignored if Shizuku provider is unavailable
        }

        setContent {
            AutostartManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    HomeScreen(
                        uiState = uiState,
                        onFilterSelected = viewModel::setFilter,
                        onToggleApp = viewModel::toggleAppAutostart,
                        onToggleShowSystemApps = viewModel::toggleShowSystemApps,
                        onRequestShizukuPermission = { ShizukuManager.requestPermission() },
                        onRefresh = {
                            viewModel.checkShizukuState()
                            viewModel.refreshApps()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            // Ignored
        }
    }
}
