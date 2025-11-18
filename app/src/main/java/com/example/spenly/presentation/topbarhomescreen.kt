package com.example.spenly.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spenly.R

private val topBarGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.9f),
        Color.Black.copy(alpha = 0.75f),
        Color(0xFF26C6DA).copy(alpha = 0.07f),


        )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    calculatorExpanded: Boolean = false,
    onCalculatorExpandedChange: (Boolean) -> Unit = {}
) {
    Box(
        modifier = Modifier.background(topBarGradient)
    ) {
        TopAppBar(
            title = { },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                Row(
                    modifier = Modifier.padding(start = 17.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Cyan)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.sparkle),
                        contentDescription = "Sparkle",
                        tint = Color.Yellow,
                        modifier = Modifier.size(27.dp)
                    )
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 17.dp)
                ) {
                    CalculatorDropdown(
                        expanded = calculatorExpanded,
                        onExpandedChange = onCalculatorExpandedChange
                    ) {
                        IconButton(
                            onClick = {
                                // Directly toggle the state
                                // This should work for both opening and closing
                                onCalculatorExpandedChange(!calculatorExpanded)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Calculator",
                                tint = if (calculatorExpanded) Color.Cyan else Color.White,
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Cyan),
                        contentAlignment = Alignment.Center
                    ) {

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Action 2",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHomescreen() {
    // Keep existing preview behavior by rendering only the top bar area
    Box { HomeTopBar() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(onClearClick: () -> Unit) {
    Box(
        modifier = Modifier.background(topBarGradient)
    ) {
        TopAppBar(
            title = { },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 17.dp)
                ) {
                    Button(
                        onClick = onClearClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFFF4D4D)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.DarkGray),
                        modifier = Modifier.height(35.dp)
                    ) {
                        Text("Clear All", fontSize = 14.sp)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(
    currentRoute: String?,
    onClearClick: () -> Unit,
    calculatorExpanded: Boolean = false,
    onCalculatorExpandedChange: (Boolean) -> Unit = {}
) {
    when (currentRoute) {
        "home" -> HomeTopBar(
            calculatorExpanded = calculatorExpanded,
            onCalculatorExpandedChange = onCalculatorExpandedChange
        )

        "history" -> HistoryTopBar(onClearClick = onClearClick)

        else -> EmptyTopBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyTopBar() {
    Box(modifier = Modifier.background(topBarGradient)) {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitledTopBar(title: String) {
    Box(modifier = Modifier.background(topBarGradient)) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    Box(
        modifier = Modifier.background(topBarGradient)
    ) {
        TopAppBar(
            title = { },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewTopHomescreen() {
    TopHomescreen()
}
