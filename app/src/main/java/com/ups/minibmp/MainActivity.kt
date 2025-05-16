package com.ups.minibmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ups.minibmp.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import com.ups.minibmp.ui.theme.MiniBMPTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniBMPTheme {
                AppNavigation()
            }
        }
    }
}
