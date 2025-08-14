package com.example.resumebuilder.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.resumebuilder.ui.navigation.AppNavHost
import com.example.resumebuilder.ui.navigation.NavDestinations
import com.example.resumebuilder.ui.theme.ResumeTheme

@Composable
fun ResumeApp() {
    ResumeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            AppNavHost(
                navController = navController,
                startDestination = NavDestinations.Welcome
            )
        }
    }
}
