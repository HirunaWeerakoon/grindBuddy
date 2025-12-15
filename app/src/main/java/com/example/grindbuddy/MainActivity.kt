package com.example.grindbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.grindbuddy.presentation.Navigation
import com.example.grindbuddy.ui.ShopScreen
import com.example.grindbuddy.ui.TimerScreen
import com.example.grindbuddy.ui.theme.GrindBuddyTheme
import com.example.grindbuddy.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrindBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Setup Dependencies
                    val navController = rememberNavController()
                    val viewModel: TimerViewModel = viewModel(factory = TimerViewModel.Factory)

                    // 2. Hand off to Navigation
                    Navigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        GrindBuddyTheme {
            ShopScreen(
                coins = 100,
                onBackClick = {}
            )
        }
    }
}




















