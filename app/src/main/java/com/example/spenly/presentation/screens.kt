package com.example.spenly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen() {
    ScreenLabel("Home Screen")
}

@Composable
fun BudgetScreen() {
    ScreenLabel("Budget Screen")
}

@Composable
fun SettingsScreen() {
    ScreenLabel("Settings Screen")
}

@Composable
private fun ScreenLabel(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0F)), // Match the consistent background color
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White)
    }
}



