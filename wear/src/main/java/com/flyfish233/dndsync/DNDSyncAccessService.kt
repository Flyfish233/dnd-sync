package com.flyfish233.dndsync

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class DNDSyncAccessService : AccessibilityService() {

    companion object {
        private var instance: DNDSyncAccessService? = null

        fun getSharedInstance(): DNDSyncAccessService? {
            return instance
        }
    }

    override fun onServiceConnected() {
        instance = this
    }

    override fun onUnbind(intent: Intent): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {

    }

    override fun onInterrupt() {

    }

    fun openQuickSettings() {
        performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    fun goHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun openNotification() {
        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    fun goBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun click(x: Float, y: Float) {
        val result = dispatchGesture(createClick(x, y), null, null)
    }

    fun clickIcon1_2() {
        val displayMetrics = resources.displayMetrics
        val gestureBuilder = GestureDescription.Builder()
        val path = Path()

        val height = displayMetrics.heightPixels
        val top = (height * .25).toInt()
        val mid = (height * .5).toInt()
        val bottom = (height * .75).toInt()
        val midX = displayMetrics.widthPixels / 2

        path.moveTo(midX.toFloat(), (height * .4).toFloat())
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 50))
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    fun swipeDown() {
        val displayMetrics = resources.displayMetrics
        val gestureBuilder = GestureDescription.Builder()
        val path = Path()

        val height = displayMetrics.heightPixels
        val top = (height * .25).toInt()
        val mid = (height * .5).toInt()
        val bottom = (height * .75).toInt()
        val midX = displayMetrics.widthPixels / 2

        path.moveTo(midX.toFloat(), 0f)
        path.lineTo(midX.toFloat(), mid.toFloat())
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 100, 50))
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    // (x, y) in screen coordinates
    private fun createClick(x: Float, y: Float): GestureDescription {
        // for a single tap a duration of 1 ms is enough
        val DURATION = 1L

        val clickPath = Path()
        clickPath.moveTo(x, y)
        val clickStroke = GestureDescription.StrokeDescription(clickPath, 0, DURATION)
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        return clickBuilder.build()
    }
}
