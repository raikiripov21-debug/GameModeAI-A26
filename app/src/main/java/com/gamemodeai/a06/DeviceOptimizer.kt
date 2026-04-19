package com.gamemodeai.a06

import android.os.Process
import kotlinx.coroutines.*
import java.io.File

/** Samsung A06 Device Optimizer — parameters exclusive to this device, never shared */
class DeviceOptimizer {
    private val DELAY_MIN_MS     = 6_000L
    private val DELAY_MAX_MS     = 7_000L
    private val ANTI_SPAM_MIN_MS = 10_000L
    private val ANTI_SPAM_MAX_MS = 12_000L
    private val THERMAL_SAFE_C   = 40.0f
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    @Volatile private var active = false

    fun onGameStarted() { active = true; scope.launch { loop() } }
    fun onGameStopped() { active = false; scope.coroutineContext.cancelChildren(); Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT) }

    private suspend fun loop() {
        while (active) {
            if (readThermalC() < THERMAL_SAFE_C) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY)
                System.gc()
            }
            delay(rand(DELAY_MIN_MS, DELAY_MAX_MS))
            delay(rand(ANTI_SPAM_MIN_MS, ANTI_SPAM_MAX_MS))
        }
    }
    private fun readThermalC() = try { val f = File("/sys/class/thermal/thermal_zone0/temp"); if (f.exists()) f.readText().trim().toFloat() / 1000f else 35f } catch (e: Exception) { 35f }
    private fun rand(min: Long, max: Long) = min + (Math.random() * (max - min)).toLong()
}
