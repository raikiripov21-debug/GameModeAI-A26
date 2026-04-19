package com.gamemodeai.a06

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.TextView

class OverlayService : Service() {
    companion object {
        const val ACTION_SHOW = "ACTION_SHOW"
        const val ACTION_HIDE = "ACTION_HIDE"
    }
    private var wm: WindowManager? = null
    private var view: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) { ACTION_SHOW -> show(); ACTION_HIDE -> hide() }
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { hide(); super.onDestroy() }

    private fun show() {
        if (view != null) return
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 8; y = 48 }
        val tv = TextView(this).apply {
            text = "GameMode AI"; textSize = 10f
            setTextColor(0xFF00FF88.toInt()); setBackgroundColor(0xCC000000.toInt()); setPadding(8,4,8,4)
        }
        view = tv; wm?.addView(tv, params)
    }
    private fun hide() { view?.let { wm?.removeViewImmediate(it); view = null } }
}
