package com.example.grindbuddy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.components.GrindMascot
import com.example.grindbuddy.components.MascotMood
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grindbuddy.components.TimerCircle
import com.example.grindbuddy.components.UserStatsBar
import com.example.grindbuddy.data.SHOP_ITEMS
import com.example.grindbuddy.presentation.Screen
import com.example.grindbuddy.presentation.theme.PrimaryPurple
import com.example.grindbuddy.presentation.theme.SoftBackground
import com.example.grindbuddy.viewmodel.TimerViewModel

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel(factory = TimerViewModel.Factory),
    onShopClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    // Collect Data
    val timeLeftSeconds by viewModel.timeLeft.collectAsState()
    val isTimerRunning by viewModel.isRunning.collectAsState()
    val isSessionFinished by viewModel.isSessionFinished.collectAsState()
    val currentXp by viewModel.totalXp.collectAsState()
    val currentCoins by viewModel.totalCoins.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()

    val level= (currentXp/100)+1
    val progress= (currentXp%100)/100f

    val equippedId by viewModel.equippedItem.collectAsState()

    val currentSkinColor = SHOP_ITEMS.find { it.id == equippedId }?.color ?: Color.Unspecified


    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(SoftBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. TOP BAR (Reusable!)
        UserStatsBar(
            xp = currentXp,
            coins = currentCoins,
            streak = currentStreak,
            onShopClick = onShopClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 2. New Pro Circle
        TimerCircle(timeLeftSeconds = timeLeftSeconds)

        Spacer(modifier = Modifier.height(50.dp))

        // 3. BUTTONS
        Button(
            onClick = { viewModel.toggleTimer() },
            modifier = Modifier
                .fillMaxWidth(0.7f) // Make it 70% width
                .height(60.dp),     // Taller button
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple
            ),
            shape = RoundedCornerShape(30.dp), // Pill shape
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Soft shadow
        ) {
            Text(
                text = if (isTimerRunning) "PAUSE FOCUS" else "START FOCUS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))        // --- MASCOT ---
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            GrindMascot(
                mood = if (isTimerRunning) MascotMood.FOCUSED else MascotMood.HAPPY,
                tint = currentSkinColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onStatsClick) {
                Text(text="stat screen")
            }
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = if(isTimerRunning) "Stay hard! Don't quit." else "Let's get this money.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // --- REWARD DIALOG ---
    if (isSessionFinished) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Session Complete!") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Focus session done.")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Show Rewards
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("XP", fontWeight = FontWeight.Bold)
                            Text("+50", color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Coins", fontWeight = FontWeight.Bold)
                            Text("+10", color = Color(0xFFD4AF37)) // Gold color
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Progress to Level ${level + 1}:")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Show the progress bar inside the dialog too
                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.claimReward() }) {
                    Text("Claim Rewards")
                }
            },
            icon = { Text("🎉", fontSize = 32.sp) }
        )
    }
    
}