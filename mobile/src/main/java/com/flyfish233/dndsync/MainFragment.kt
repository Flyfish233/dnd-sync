package com.flyfish233.dndsync

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainFragment : PreferenceFragmentCompat() {
    private lateinit var dndPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        dndPref = findPreference("dnd_permission_key")!!

        dndPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (!checkDNDPermission()) {
                openDNDPermissionRequest()
            } else {
                Toast.makeText(context, R.string.dnd_permission_allowed, Toast.LENGTH_SHORT)
                    .show()
            }
            true
        }
        checkDNDPermission()

        findPreference<Preference>("help")!!.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    android.net.Uri.parse("https://help.wearosbox.com/faq/app/dnd-sync.html")
                startActivity(intent)
                true
            }

        findPreference<Preference>("github")!!.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    android.net.Uri.parse("https://github.com/Flyfish233/dnd-sync/")
                startActivity(intent)
                true
            }
        if (context?.packageManager?.getLaunchIntentForPackage("com.google.android.apps.wellbeing") != null)
            findPreference<Preference>("bedtime_sync_key")!!.isSelectable = true
    }


    private fun checkDNDPermission(): Boolean {
        val mNotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val allowed = mNotificationManager.isNotificationPolicyAccessGranted
        if (allowed) {
            dndPref.setSummary(R.string.dnd_permission_allowed)
        } else {
            dndPref.setSummary(R.string.dnd_permission_not_allowed)
        }
        return allowed
    }

    private fun openDNDPermissionRequest() {
        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}
