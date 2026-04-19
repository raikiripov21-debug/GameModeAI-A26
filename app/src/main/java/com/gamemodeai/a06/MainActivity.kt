package com.gamemodeai.a06

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.gamemodeai.a06.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(
                    hasOverlayPermission = { Settings.canDrawOverlays(this) },
                    hasUsagePermission = { hasUsageStatsPermission() },
                    onRequestOverlay = { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) },
                    onRequestUsage = { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
                    onStartService = { startForegroundService(Intent(this, GameDetectionService::class.java)) },
                    onStopService = { stopService(Intent(this, GameDetectionService::class.java)) }
                )
            }
        }
    }
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
