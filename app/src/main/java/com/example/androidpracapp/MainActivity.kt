package com.example.androidpracapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.androidpracapp.data.navigation.NavigationApp
import com.example.androidpracapp.ui.theme.AndroidPracAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPracAppTheme {
                val navController = rememberNavController()
                NavigationApp(navController)
            }
        }
    }
}