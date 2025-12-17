package com.example.grindbuddy.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.presentation.theme.PrimaryPurple

@Composable
fun ChallengeCard(
    title: String,
    description: String,
    currentProgress: Int,
    targetProgress: Int,
    onClaimClick: () -> Unit
) {
    // 1. Calculate Math
    // coerceIn(0f, 1f) ensures the bar doesn't explode if we have 500/400 mins
    val isCompleted = currentProgress >= targetProgress
    val progressFraction = (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // HEADER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black // Or Color.White if using Dark Mode
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // REWARD BUTTON vs PERCENTAGE
                if (isCompleted) {
                    Button(
                        onClick = onClaimClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)), // Gold Color
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("CLAIM", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PROGRESS BAR
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isCompleted) Color(0xFFD4AF37) else PrimaryPurple,
                trackColor = Color.LightGray,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            // STATUS TEXT (e.g., "120 / 240 mins")
            Text(
                text = "$currentProgress / $targetProgress",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}