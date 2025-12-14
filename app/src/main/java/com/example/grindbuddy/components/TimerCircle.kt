package com.example.grindbuddy.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.presentation.theme.*

@Composable
fun TimerCircle(
    timeLeftSeconds: Long,
    totalTimeSeconds: Long = 25 * 60L // Default 25 mins
) {
    val progress = timeLeftSeconds / totalTimeSeconds.toFloat()

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        // 1. The Background Track (Gray Circle)
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = ProgressTrack, // Very light gray
            strokeWidth = 20.dp,
        )

        // 2. The Progress Indicator (Colorful)
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = PrimaryPurple,
            strokeWidth = 20.dp,
            strokeCap = StrokeCap.Round, // Rounded ends look premium
        )

        // 3. The Text inside
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedTime,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "Remaining",
                fontSize = 14.sp,
                color = TextGray
            )
        }
    }
}