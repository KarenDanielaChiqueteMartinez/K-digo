package com.example.kodelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kodelearn.navigation.KodeLearnNavigation
import com.example.kodelearn.ui.theme.KodeLearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val application = application as KodeLearnApplication
        
        setContent {
            KodeLearnTheme {
                KodeLearnNavigation(
                    repository = application.repository
                )
            }
        }
    }
}
