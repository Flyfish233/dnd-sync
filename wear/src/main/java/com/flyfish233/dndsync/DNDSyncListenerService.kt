package com.flyfish233.dndsync

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DNDSyncListenerService : WearableListenerService() {
    companion object {
        private const val TAG = "DNDSyncListenerService"
        private const val DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onMessageReceived: $messageEvent")
        }
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (messageEvent.path.equals(DND_SYNC_MESSAGE_PATH, ignoreCase = true)) {
            Log.d(TAG, "received path: $DND_SYNC_MESSAGE_PATH")

            val vibrate = prefs.getBoolean("vibrate_key", false)
            Log.d(TAG, "vibrate: $vibrate")
            if (vibrate) {
                vibrate()
            }

            val data: ByteArray = messageEvent.data
            // data[0] contains dnd mode of phone
            // 0 = INTERRUPTION_FILTER_UNKNOWN
            // 1 = INTERRUPTION_FILTER_ALL (all notifications pass)
            // 2 = INTERRUPTION_FILTER_PRIORITY
            // 3 = INTERRUPTION_FILTER_NONE (no notification passes)
            // 4 = INTERRUPTION_FILTER_ALARMS
            // Custom
            // 5 = BedTime Mode On
            // 6 = BedTime Mode Off
            val dndStatePhone: Byte = data[0]
            Log.d(TAG, "dndStatePhone: $dndStatePhone")

            // get dnd state
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val filterState = mNotificationManager.currentInterruptionFilter
            if (filterState < 0 || filterState > 4) {
                Log.d(TAG, "DNDSync weird current dnd state: $filterState")
            }
            val currentDndState: Byte = filterState.toByte()
            Log.d(TAG, "currentDndState: $currentDndState")

            if (dndStatePhone == 5.toByte() || dndStatePhone == 6.toByte()) {
                val useBedtimeMode = prefs.getBoolean("bedtime_key", true)
                Log.d(TAG, "useBedtimeMode: $useBedtimeMode")
                if (useBedtimeMode) {
                    toggleBedtimeMode()
                }
            }

            if (dndStatePhone != currentDndState) {
                Log.d(TAG, "dndStatePhone != currentDndState: $dndStatePhone != $currentDndState")
                // set DND anyways, also in case bedtime toggle does not work to have at least DND
                if (mNotificationManager.isNotificationPolicyAccessGranted) {
                    mNotificationManager.setInterruptionFilter(dndStatePhone.toInt())
                    Log.d(TAG, "DND set to $dndStatePhone")
                } else {
                    Log.d(TAG, "attempting to set DND but access not granted")
                }
            }
        } else {
            super.onMessageReceived(messageEvent)
        }
    }

    private fun toggleBedtimeMode() {
        val serv: DNDSyncAccessService? = DNDSyncAccessService.getSharedInstance()
        if (serv == null) {
            Log.d(TAG, "accessibility not connected")
            // create a handler to post messages to the main thread
            val mHandler = Handler(mainLooper)
            mHandler.post {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.acc_not_connected),
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }

        Log.d(TAG, "accessibility connected. Perform toggle.")
        // turn on screen
        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "dndsync:MyWakeLock"
        )
        wakeLock.acquire(2 * 60 * 1000L /*2 minutes*/)

        // create a handler to post messages to the main thread
        val mHandler = Handler(mainLooper)
        mHandler.post {
            Toast.makeText(
                applicationContext, resources.getString(R.string.bedtime_toggle), Toast.LENGTH_SHORT
            ).show()
        }

        // wait a bit before touch input to make sure screen is on
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // open quick panel
        serv.swipeDown()

        // wait for quick panel to open
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // click on the middle icon in the first row
        serv.clickIcon1_2()

        // wait a bit
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // close quick panel
        serv.goBack()

        wakeLock.release()
    }

    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
