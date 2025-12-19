package com.example.grindbuddy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.grindbuddy.ui.TimerScreen
import com.example.grindbuddy.ui.StatsScreen
import com.example.grindbuddy.viewmodel.TimerViewModel

@Composable
fun Navigation( // You might want to rename this to "GrindNavigation" to match previous steps
    navController: NavHostController,
    viewModel: TimerViewModel
) {
    val coins by viewModel.totalCoins.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ){
        composable(route = Screen.Timer.route){
            TimerScreen(
                viewModel = viewModel,
                onShopClick = {
                    navController.navigate(Screen.Shop.route)
                },
                onStatsClick = { // <--- 3. ADD THIS HANDLER
                    navController.navigate(Screen.Stats.route)
                }
            )
        }
        composable(route = Screen.Shop.route){
            // 1. Collect the Inventory Data
            val coins by viewModel.totalCoins.collectAsState() // Already had this
            val ownedItems by viewModel.ownedItems.collectAsState() // <--- NEW
            val equippedId by viewModel.equippedItem.collectAsState() // <--- NEW

            ShopScreen(
                coins = coins,
                ownedItems = ownedItems,
                equippedId = equippedId,
                onBuyClick = { item -> viewModel.purchaseItem(item) }, // <--- Action
                onEquipClick = { id -> viewModel.selectItem(id) },     // <--- Action
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Stats.route) {
            // We need to collect the history from the ViewModel
            val history by viewModel.sessionHistory.collectAsState()
            val weeklyStats by viewModel.weeklyStats.collectAsState()
            val minutesToday by viewModel.minutesToday.collectAsState()
            val dailyTarget by viewModel.dailyTarget.collectAsState()
            val minutesThisWeek by viewModel.minutesThisWeek.collectAsState()
            val weeklyTarget by viewModel.weeklyTarget.collectAsState()
            val currentStreak by viewModel.currentStreak.collectAsState()
            val streakTarget by viewModel.streakTarget.collectAsState()

            StatsScreen(
                history = history,
                weeklyStats = weeklyStats,
                minutesToday = minutesToday,
                dailyTarget = dailyTarget,
                onClaimDaily = { viewModel.claimDailyQuest() },
                minutesThisWeek = minutesThisWeek,
                weeklyTarget = weeklyTarget,
                onClaimWeekly = { viewModel.claimWeeklyQuest() },
                currentStreak = currentStreak,
                streakTarget = streakTarget,
                onClaimStreak = { viewModel.claimStreakQuest() },
                onBackClick = { navController.popBackStack() }
            )

        }
    }
}