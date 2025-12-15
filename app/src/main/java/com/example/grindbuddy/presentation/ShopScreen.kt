package com.example.grindbuddy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.presentation.theme.GrindBlack
import com.example.grindbuddy.presentation.theme.GrindSurface
import com.example.grindbuddy.presentation.theme.GrindWhite

@Composable
fun ShopScreen(
    coins: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrindSurface)
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "🛒 THE SHOP", fontSize = 32.sp, color = GrindWhite)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "You have $coins Coins", color = GrindWhite)

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBackClick) {
            Text("Go Back to Grind")
        }
    }
}
