package com.gamemodeai.a06

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.*
import android.os.*
import androidx.core.app.NotificationCompat

class GameDetectionService : Service() {
    companion object {
        const val TARGET_PACKAGE = "com.dts.freefire"
        const val CHANNEL_ID = "gmai_detection"
        const val NOTIF_ID = 1001
        const val POLL_MS = 2_000L
    }
    private val handler = Handler(Looper.getMainLooper())
    private val optimizer = DeviceOptimizer()
    private var running = false
    private var gameActive = false
    private val poll = object : Runnable {
        override fun run() {
            if (!running) return
            val inGame = isTargetForeground()
            if (inGame && !gameActive) {
                gameActive = true
                optimizer.onGameStarted()
                startService(Intent(this@GameDetectionService, OverlayService::class.java).also { it.action = OverlayService.ACTION_SHOW })
            } else if (!inGame && gameActive) {
                gameActive = false
                optimizer.onGameStopped()
                startService(Intent(this@GameDetectionService, OverlayService::class.java).also { it.action = OverlayService.ACTION_HIDE })
            }
            handler.postDelayed(this, POLL_MS)
        }
    }
    override fun onCreate() { super.onCreate(); createChannel() }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("GameMode AI").setContentText("Monitoring...").setSmallIcon(android.R.drawable.ic_media_play).setOngoing(true).build())
        running = true; handler.post(poll); return START_STICKY
    }
    override fun onDestroy() { running = false; handler.removeCallbacks(poll); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun isTargetForeground(): Boolean = try {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 5_000L, now)?.maxByOrNull { it.lastTimeUsed }?.packageName == TARGET_PACKAGE
    } catch (e: Exception) { false }
    private fun createChannel() {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel(CHANNEL_ID, "GameMode AI", NotificationManager.IMPORTANCE_LOW))
    }
}
