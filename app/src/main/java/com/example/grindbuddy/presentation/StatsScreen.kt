package com.example.grindbuddy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.grindbuddy.components.ChallengeCard
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
    weeklyStats: List<Pair<String, Int>>,
    minutesToday: Int,
    dailyTarget: Int,
    onClaimDaily: () -> Unit,
    minutesThisWeek: Int,
    weeklyTarget: Int,
    onClaimWeekly: () -> Unit,
    currentStreak: Int,
    streakTarget: Int,
    onClaimStreak: () -> Unit,
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
            0 -> WeeklyGraphView(weeklyStats)  // Weekly
            1 -> MonthlyGraphView() // Monthly (Placeholder)
            2 -> HistoryListView(history) // The List
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) { // <--- ADD SCROLLING!
            WeeklyGraphView(weeklyStats)

            Spacer(modifier = Modifier.height(20.dp))
            Text("🏆 ACTIVE QUESTS", color = Color.White, fontWeight = FontWeight.Bold)

            // 1. Daily Card
            ChallengeCard(
                title = "Daily Grind",
                description = "Study for ${dailyTarget} minutes today.",
                currentProgress = minutesToday,
                targetProgress = dailyTarget,
                onClaimClick = onClaimDaily
            )

            // 2. Weekly Card (NEW)
            ChallengeCard(
                title = "Weekly Warrior",
                description = "Study for ${weeklyTarget} minutes this week.",
                currentProgress = minutesThisWeek,
                targetProgress = weeklyTarget,
                onClaimClick = onClaimWeekly
            )

            // 3. Streak Card (NEW)
            ChallengeCard(
                title = "Streak Master",
                description = "Reach a ${streakTarget}-day streak.",
                currentProgress = currentStreak,
                targetProgress = streakTarget,
                onClaimClick = onClaimStreak
            )

            Spacer(modifier = Modifier.height(20.dp)) // Extra space at bottom
        }

    }
}

// --- SUB-COMPONENTS ---

@Composable
fun WeeklyGraphView(data: List<Pair<String, Int>>) {
    // 1. Math: Find the max value to scale the bars

    val maxDataValue = data.maxOfOrNull { it.second } ?: 0
    val safeMax = maxOf(maxDataValue, 400)

    // We define a fixed height for the drawing area so bars know how tall to be
    val graphHeight = 150.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = GrindSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- LEFT: Y-AXIS (0m to Max) ---
            Column(
                modifier = Modifier
                    .height(graphHeight)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${safeMax}m", fontSize = 12.sp, color = Color.Gray)
                Text(text = "${safeMax / 2}m", fontSize = 12.sp, color = Color.Gray)
                Text(text = "0m", fontSize = 12.sp, color = Color.Gray)
            }

            // --- RIGHT: BARS + DAYS ---
            Column(modifier = Modifier.weight(1f)) {

                // PART 1: The Bars (Aligned to Bottom)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(graphHeight), // Fixed drawing height
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom // Grow from floor
                ) {
                    data.forEach { (_, minutes) ->
                        // Calculate exact height: (Minutes / Max) * TotalHeight
                        val heightFraction = minutes / safeMax.toFloat()
                        val barHeight = graphHeight * heightFraction

                        // If 0 minutes, show a tiny dot so user sees the day exists
                        val actualBarHeight = if (minutes > 0) barHeight else 4.dp
                        val barColor = if (minutes > 0) PrimaryPurple else Color.DarkGray.copy(alpha = 0.3f)

                        Box(
                            modifier = Modifier
                                .width(16.dp) // Thinner bars look cleaner
                                .height(actualBarHeight)
                                .background(
                                    color = barColor,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // PART 2: The X-Axis Labels (Days)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    data.forEach { (day, _) ->
                        Text(
                            text = day, // "Mon", "Tue"
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.width(24.dp), // Fixed width to center text
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
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
