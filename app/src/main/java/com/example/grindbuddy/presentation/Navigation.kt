package com.example.grindbuddy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.grindbuddy.ui.shopScreen
import com.example.grindbuddy.viewmodel.TimerViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    viewModel: TimerViewModel
) {
    // Collect data needed for multiple screens here (optional)
    val coins by viewModel.totalCoins.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ){
        composable(route = Screen.Timer.route){
            timerScreen(
                viewModel = viewModel,
                onShopClick = {
                    navController.navigate(Screen.Shop.route)
                }
            )
        }
        composable(route = Screen.Shop.route){
            shopScreen(
                viewModel = viewModel,
                coins = coins,
                onBackClick = {
                    navController.navigate(Screen.Timer.route)
                }
            )

        }


    }
}