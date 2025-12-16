package com.example.grindbuddy.presentation

sealed class  Screen (val route:String){
    object Timer:Screen("main_screen")
    object Shop:Screen("shop_screen")

    object Stats : Screen("stats_screen")

}