package com.example.grindbuddy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import com.example.grindbuddy.presentation.theme.*

@Composable
fun UserStatsBar(
    xp: Int,
    coins: Int,
    streak: Int,
    onShopClick: () -> Unit
) {
    val level = (xp / 100) + 1
    val xpInCurrentLevel = xp % 100
    val progress = xpInCurrentLevel / 100f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // LEFT: The "Profile" feel
        Column {
            Text(
                text = "Welcome Back,",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
            Text(
                text = "Grinder #1", // We can change this name later
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "Level: $level",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = TextBlack
            )
        }

        // RIGHT: The Stats (Coins & Fire) in a nice container
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { onShopClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = "🔥 $streak  |  💰 $coins", fontWeight = FontWeight.Bold, color = TextBlack)
        }
    }
}