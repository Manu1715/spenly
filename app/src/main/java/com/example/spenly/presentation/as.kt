package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Settings2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Text(
            text = "Settings",
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))

        val gradientBrush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF8E2DE2).copy(0.8f), Color(0xFF4A00E0).copy(0.7f),Color.Red.copy(0.3f))
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 32.dp, vertical = 5.dp),
            shape = RoundedCornerShape(17.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBrush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(13.dp, 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Notifications",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(7.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            "Spenly Premium",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "Tap to learn more",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                        )
                    }

                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Account",
            color = Color.LightGray,
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(15.dp, 1.dp),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray.copy(0.5f),
                contentColor = Color.White

            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Cyan.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Account",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(17.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Account", color = Color.White,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text("My Account")
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Go to account",
                        modifier = Modifier.size(20.dp),
                        tint = Color.LightGray
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 10.dp),
                    thickness = 0.7.dp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Green.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Account Transfer",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(17.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Account Transfer", color = Color.White,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text("Transfer money between accounts")
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Go to account transfer",
                        modifier = Modifier.size(20.dp),
                        tint = Color.LightGray
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 10.dp),
                    thickness = 0.7.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Drive Sync",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Cyan
                    )

                    Spacer(modifier = Modifier.width(17.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Drive Sync", color = Color.White,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text("Last sync:11s ago")
                    }

                    var isChecked by remember { mutableStateOf(true) }


                    Switch(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )

                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 10.dp),
                    thickness = 0.7.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Spenly",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Cyan
                    )

                    Spacer(modifier = Modifier.width(17.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Share Spenly", color = Color.White,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text("Invite your friends to Spenly")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 10.dp),
                    thickness = 0.7.dp,
                    color = Color.White
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettings2() {
    Settings2()
}
