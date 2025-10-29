package com.example.spenly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spenly.presentation.BottomBar
import com.example.spenly.presentation.HomeTopBar
import com.example.spenly.presentation.screens.BudgetScreen
import com.example.spenly.presentation.HistoryScreen
import com.example.spenly.presentation.screens.Homescreen

import com.example.spenly.presentation.screens.SettingsScreen
import com.example.spenly.ui.theme.SpenlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpenlyTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    containerColor = Color(0xFF0C0C0F), // Consistent background color
                    topBar = { 
                        when (currentRoute) {
                            "history" -> { /* History screen has its own TopAppBar */ }
                            else -> { HomeTopBar() }
                        }
                    },
                    bottomBar = { BottomBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { Homescreen() }
                        composable("history") { HistoryScreen() }
                        composable("budget") { BudgetScreen() }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}

