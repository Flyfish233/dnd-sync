package com.flyfish233.dndsync

import android.content.SharedPreferences
import android.service.notification.NotificationListenerService
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

        //TODO enable/disable service based on app setting to save battery
//        // We don't want to run a background service so disable and stop it
//        // to avoid running this service in the background
//        disableServiceComponent()
//        Log.i(TAG, "Disabling service")
//
//        try {
//            stopSelf()
//        } catch(SecurityException e) {
//            Log.e(TAG, "Failed to stop service")
//        }
    }

//    private fun disableServiceComponent() {
//        val p = packageManager
//        val componentName = ComponentName(this, DNDNotificationService::class.java)
//        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
//    }

    override fun onListenerDisconnected() {
        Log.d(TAG, "listener disconnected")
        running = false
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
                Wearable.getCapabilityClient(this)
                    .getCapability(DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE)
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
                    val data = ByteArray(2)
                    data[0] = dndState.toByte()
                    val sendTask: Task<Int> = Wearable.getMessageClient(this)
                        .sendMessage(node.id, DND_SYNC_MESSAGE_PATH, data)

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
