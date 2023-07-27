package com.flyfish233.dndsync;

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class MainFragment : PreferenceFragmentCompat() {
    private lateinit var dndPref: Preference
    private lateinit var accPref: Preference
    private lateinit var bedtimePref: SwitchPreferenceCompat

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        dndPref = findPreference("dnd_permission_key")!!
        accPref = findPreference("acc_permission_key")!!
        bedtimePref = findPreference("bedtime_key")!!

        dndPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (!checkDNDPermission()) {
                Toast.makeText(
                    context,
                    R.string.follow_instruction_to_allow_dnd_permission,
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }

        accPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (!checkAccessibilityService()) {
                openAccessibility()
            }
            true
        }



        checkDNDPermission()
        checkAccessibilityService()
    }

    private fun checkAccessibilityService(): Boolean {
        val serv = DNDSyncAccessService.getSharedInstance()
        val connected = serv != null
        val manufacturer = android.os.Build.MANUFACTURER
        if (manufacturer.equals("samsung", ignoreCase = true)) {
            // Samsung specific feature
            if (connected) {
                accPref.setSummary(R.string.acc_permission_allowed)
                bedtimePref.isEnabled = true
            } else {
                accPref.setSummary(R.string.acc_permission_not_allowed)
                bedtimePref.isEnabled = false
                bedtimePref.isChecked = false
            }
        } else {
            bedtimePref.isEnabled = false
        }
        return connected
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

    private fun openAccessibility() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
}
