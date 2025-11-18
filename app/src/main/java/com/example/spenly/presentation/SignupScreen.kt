package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SignupScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Red.copy(0.4f)),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(text = "Signup Screen", modifier = Modifier.padding(16.dp))
    }
}

@Composable
@Preview()
fun SignUpPreview(){
    SignupScreen()
}

