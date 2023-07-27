package com.flyfish233.dndsync

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DNDSyncListenerService : WearableListenerService() {
    companion object {
        private const val TAG = "DNDSyncListenerService"
        private const val DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived: $messageEvent")

        if (messageEvent.path.equals(DND_SYNC_MESSAGE_PATH, ignoreCase = true)) {
            Log.d(TAG, "received path: $DND_SYNC_MESSAGE_PATH")

            val data: ByteArray = messageEvent.data
            // data[0] contains dnd mode of phone
            // 0 = INTERRUPTION_FILTER_UNKNOWN
            // 1 = INTERRUPTION_FILTER_ALL (all notifications pass)
            // 2 = INTERRUPTION_FILTER_PRIORITY
            // 3 = INTERRUPTION_FILTER_NONE (no notification passes)
            // 4 = INTERRUPTION_FILTER_ALARMS
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

            if (dndStatePhone != currentDndState) {
                Log.d(TAG, "dndStatePhone != currentDndState: $dndStatePhone != $currentDndState")
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
}
