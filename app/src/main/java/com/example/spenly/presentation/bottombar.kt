package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val lightPurple = Color(0xFFD0BCFF)
    val unselectedColor = Color(0xFF9E9E9E)
    val darkgrey = Color(0xFF1A1A1A)

    val items = listOf("Home", "History", "Budget", "Settings")
    val routes = listOf("home", "history", "budget", "settings")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.History,
        Icons.Default.PieChart,
        Icons.Default.Settings
    )

    // This composable only renders the bottom bar UI. The Scaffold is provided by MainActivity
    Box {
        NavigationBar(
            modifier = Modifier
                .background(
                    color = Color.Black
                )
                .height(75.dp),
            containerColor = Color.Transparent
        ) {
                    items.forEachIndexed { index, screen ->

                        if (index == 2) {
                            Spacer(Modifier.weight(1f))
                        }

                        val route = routes[index]
                        val isSelected = currentRoute == route

                        NavigationBarItem(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            selected = isSelected,
                            onClick = {
                                // Prevent navigation to current destination
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        // For bottom navigation: keep all screens in stack
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = icons[index],
                                    contentDescription = screen
                                )
                            },
                            label = { Text(screen) },

                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Cyan,
                                selectedTextColor = Color.Cyan,
                                unselectedIconColor = unselectedColor,
                                unselectedTextColor = unselectedColor,

                                indicatorColor = lightPurple.copy(alpha = 0.3f)
                            )
                        )
                    }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    color = darkgrey.copy(0.9f)
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                navController.navigate("add") {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Cyan,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


