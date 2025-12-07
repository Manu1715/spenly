package com.example.spenly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spenly.presentation.BottomBar
import com.example.spenly.presentation.BudgetScreen
import com.example.spenly.presentation.AddTransactionScreen
import com.example.spenly.presentation.HistoryScreen
import com.example.spenly.presentation.LocalClearDialogTrigger
import com.example.spenly.data.ProvideTransactionRepository
import com.example.spenly.presentation.SettingsScreen
import com.example.spenly.presentation.ScreenTopBar
import com.example.spenly.presentation.screens.Homescreen

import com.example.spenly.ui.theme.SpenlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpenlyTheme {
                ProvideTransactionRepository(context = this@MainActivity) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val clearDialogTrigger = remember { mutableStateOf(false) }
                    var calculatorExpanded by remember { mutableStateOf(false) }

                    CompositionLocalProvider(LocalClearDialogTrigger provides clearDialogTrigger) {
                    Scaffold(
                        containerColor = Color(0xFF0C0C0F), // Consistent background color
                        topBar = {
                            ScreenTopBar(
                                currentRoute = currentRoute,
                                onClearClick = { clearDialogTrigger.value = true },
                                calculatorExpanded = calculatorExpanded,
                                onCalculatorExpandedChange = { calculatorExpanded = it }
                            )
                        },
                        bottomBar = { BottomBar(navController = navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(
                                route = "home"
                            ) {
                                Homescreen()
                                }
                            composable(
                                route = "history"
                            ) { HistoryScreen() }
                            composable(
                                route = "budget"
                            ) { BudgetScreen() }
                            composable(
                                route = "settings"
                            ) { SettingsScreen() }
                            composable(
                                route = "add",
                                enterTransition = {
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = tween(180)
                                    ) + fadeIn(animationSpec = tween(180))
                                },
                                exitTransition = {
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(160)
                                    ) + fadeOut(animationSpec = tween(160))
                                },
                                popEnterTransition = {
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = tween(180)
                                    ) + fadeIn(animationSpec = tween(180))
                                },
                                popExitTransition = {
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(160)
                                    ) + fadeOut(animationSpec = tween(160))
                                }
                            ) {
                                AddTransactionScreen(
                                    onCancel = { navController.popBackStack() },
                                    onSave = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}}

