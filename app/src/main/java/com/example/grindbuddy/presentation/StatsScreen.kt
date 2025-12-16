package com.example.grindbuddy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.data.FocusSession
import com.example.grindbuddy.presentation.theme.GrindSurface
import com.example.grindbuddy.presentation.theme.GrindWhite
import com.example.grindbuddy.presentation.theme.PrimaryPurple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    history: List<FocusSession>,
    onBackClick: () -> Unit
) {
    // 1. STATE: Which tab is selected? (0 = Weekly, 1 = Monthly, 2 = History)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly", "Monthly", "History")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBackClick) { Text("<") }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "📊 STATISTICS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. THE TABS
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PrimaryPurple,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryPurple
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) PrimaryPurple else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. THE SWITCHER
        when (selectedTab) {
            0 -> WeeklyGraphView()  // Weekly
            1 -> MonthlyGraphView() // Monthly (Placeholder)
            2 -> HistoryListView(history) // The List
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun WeeklyGraphView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(GrindSurface, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("📈 Graph Coming Soon", color = Color.Gray)
    }
}

@Composable
fun MonthlyGraphView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(GrindSurface, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("📅 Monthly View", color = Color.Gray)
    }
}

@Composable
fun HistoryListView(history: List<FocusSession>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(history) { session ->
            HistoryItem(session)
        }
    }
}

// 👇 THIS IS THE MISSING PART 👇
@Composable
fun HistoryItem(session: FocusSession) {
    val dateString = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(session.dateTimestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrindSurface, RoundedCornerShape(8.dp))
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