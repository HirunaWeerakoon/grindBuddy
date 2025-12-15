package com.example.grindbuddy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.grindbuddy.ui.TimerScreen
import com.example.grindbuddy.ui.ShopScreen
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
            // FIX 1: Use Capital 'T' (TimerScreen)
            TimerScreen(
                viewModel = viewModel,
                onShopClick = {
                    navController.navigate(Screen.Shop.route)
                }
            )
        }
        composable(route = Screen.Shop.route){
            ShopScreen(
                coins = coins,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}