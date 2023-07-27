package com.flyfish233.dndsync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.theme.applyStyle(
            rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true
        );
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
