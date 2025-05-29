package com.example.koalaappm13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.koalaappm13.ui.KoalaAppM13Theme
import com.example.koalaappm13.ui.TermsAndConditionsScreen

class TermsAndConditionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoalaAppM13Theme {
                TermsAndConditionsScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}