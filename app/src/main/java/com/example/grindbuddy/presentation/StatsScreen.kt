package com.example.grindbuddy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.data.FocusSession
import com.example.grindbuddy.presentation.theme.GrindSurface
import com.example.grindbuddy.presentation.theme.GrindWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    history: List<FocusSession>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // or GrindBlack
            .padding(16.dp)
    ) {
        Text(text = "📊 SESSION HISTORY", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        // THE LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { session ->
                HistoryItem(session)
            }
        }
    }
}

@Composable
fun HistoryItem(session: FocusSession) {
    // Convert timestamp to readable date
    val dateString = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(session.dateTimestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .background(GrindSurface, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = dateString, color = Color.Gray, fontSize = 12.sp)
            Text(text = "${session.durationMinutes} mins", color = GrindWhite, fontWeight = FontWeight.Bold)
        }
        Text(text = "+${session.xpEarned} XP", color = Color.Yellow, fontWeight = FontWeight.Bold)
    }
}