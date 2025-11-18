package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    // 1. Use LazyColumn for a scrollable and performant layout
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Consistent spacing
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Premium Card
        item {
            PremiumCard()
        }

        // Account Section Header
        item {
            Text(
                "Account",
                color = Color.LightGray,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp), // Add top padding
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Account Settings Card
        item {
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Person,
                    iconBackgroundColor = Color.Cyan.copy(alpha = 0.3f),
                    title = "Account",
                    subtitle = "My Account"
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Go to account",
                        modifier = Modifier.size(20.dp),
                        tint = Color.LightGray
                    )
                }

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.SwapHoriz,
                    iconBackgroundColor = Color.Green.copy(alpha = 0.3f),
                    title = "Account Transfer",
                    subtitle = "Transfer money between accounts"
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Go to account transfer",
                        modifier = Modifier.size(20.dp),
                        tint = Color.LightGray
                    )
                }

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.Cloud,
                    iconBackgroundColor = Color.Transparent, // No background for this one
                    title = "Drive Sync",
                    subtitle = "Last sync: 11s ago",
                    iconSize = 28.dp ,
                    iconTint = Color.Cyan, // Custom icon color
                ) {
                    var isChecked by remember { mutableStateOf(true) }
                    Switch(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                }

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.Share,
                    iconBackgroundColor = Color.Transparent, // No background for this one
                    title = "Share Spenly",
                    subtitle = "Invite your friends to Spenly",
                    iconSize = 24.dp, // Custom icon size
                    iconTint = Color.Cyan // Custom icon color
                )
            }
        }
    }
}

// 2. Extracted the Premium Card into its own composable
@Composable
private fun PremiumCard() {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF8E2DE2).copy(0.8f), Color(0xFF4A00E0).copy(0.7f),Color.Red.copy(0.3f))
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp), // Use standard padding
        shape = RoundedCornerShape(17.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(elevation = 10.dp, shape = CircleShape)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column {
                Text(
                    "Spenly Premium",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Tap to learn more",
                    color = Color.White.copy(alpha = 0.7f), // Slightly more muted
                    fontSize = 12.sp,
                )
            }
        }
    }
}

// 3. Created a generic container card for settings
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(0.5f),
            contentColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

// 4. Created a highly reusable SettingsItem composable
@Composable
private fun SettingsItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    subtitle: String,
    iconSize: Dp = 20.dp,
    iconTint: Color = Color.White.copy(alpha = 0.9f), // --- CHANGE 1: Added iconTint parameter ---
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(iconSize),
                tint = iconTint // --- CHANGE 2: Use the iconTint parameter ---
            )
        }

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 18.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }

        // Action
        if (action != null) {
            action()
        }
    }
}

// 5. Created a reusable divider for consistency
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 64.dp, end = 16.dp), // Aligned with text
        thickness = 1.dp,
        color = Color.Gray.copy(alpha = 0.2f)
    )
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
