package com.flyfish233.dndsync

import android.content.SharedPreferences
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException

class DNDNotificationService : NotificationListenerService() {
    companion object {
        private const val TAG = "DNDNotificationService"
        private const val DND_SYNC_CAPABILITY_NAME = "dnd_sync"
        private const val DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync"
        var running = false
    }

    override fun onListenerConnected() {
        Log.d(TAG, "listener connected")
        running = true
    }

    override fun onListenerDisconnected() {
        Log.d(TAG, "listener disconnected")
        running = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        onNotificationAddedOrRemovedCallDNDSync(sbn, 5)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        onNotificationAddedOrRemovedCallDNDSync(sbn, 6)
    }

    private fun onNotificationAddedOrRemovedCallDNDSync(
        sbn: StatusBarNotification, interruptionFilter: Int
    ) {
        if (sbn.packageName == "com.google.android.apps.wellbeing") {
            val title = sbn.notification.extras.getString("android.title")
            if (title == "Bedtime mode is on") {
                val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val syncBedTime = prefs.getBoolean("bedtime_sync_key", true)
                //BedTime
                if (syncBedTime) {
                    Thread {
                        sendDNDSync(interruptionFilter)
                    }.start()
                }
            }
        }
    }

    override fun onInterruptionFilterChanged(interruptionFilter: Int) {
        Log.d(TAG, "interruption filter changed to $interruptionFilter")

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val syncDnd = prefs.getBoolean("dnd_sync_key", true)
        if (syncDnd) {
            Thread {
                sendDNDSync(interruptionFilter)
            }.start()
        }
    }

    private fun sendDNDSync(dndState: Int) {
        // https://developer.android.com/training/wearables/data/messages

        // search nodes for sync
        val capabilityInfo: CapabilityInfo?
        try {
            capabilityInfo = Tasks.await(
                Wearable.getCapabilityClient(this).getCapability(
                    DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE
                )
            )
        } catch (e: ExecutionException) {
            e.printStackTrace()
            Log.e(TAG, "execution error while searching nodes", e)
            return
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e(TAG, "interruption error while searching nodes", e)
            return
        }

        // send request to all reachable nodes
        // capabilityInfo has the reachable nodes with the dnd sync capability
        val connectedNodes: Set<Node> = capabilityInfo.nodes
        if (connectedNodes.isEmpty()) {
            // Unable to retrieve node with transcription capability
            Log.d(TAG, "Unable to retrieve node with sync capability!")
        } else {
            for (node in connectedNodes) {
                if (node.isNearby) {
                    val data = byteArrayOf(dndState.toByte())
                    val sendTask: Task<Int> = Wearable.getMessageClient(this).sendMessage(
                        node.id, DND_SYNC_MESSAGE_PATH, data
                    )

                    sendTask.addOnSuccessListener {
                        Log.d(TAG, "send successful! Receiver node id: " + node.id)
                    }

                    sendTask.addOnFailureListener {
                        Log.d(TAG, "send failed! Receiver node id: " + node.id)
                    }
                }
            }
        }
    }
}
