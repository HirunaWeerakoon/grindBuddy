package com.example.grindbuddy.data

import androidx.compose.ui.graphics.Color

data class ShopItem(
    val id: String,        // Unique ID (e.g., "skin_blue")
    val name: String,      // Display Name (e.g., "Blue Slime")
    val price: Int,        // Cost (e.g., 100)
    val color: Color       // The Color we will apply to the mascot
)

// THE CATALOG (Hardcoded list of things to sell)
val SHOP_ITEMS = listOf(
    ShopItem("skin_default", "Original", 0, Color.Unspecified), // Unspecified = No Tint
    ShopItem("skin_blue", "Ice Cold", 100, Color(0xFF64B5F6)),
    ShopItem("skin_green", "Forest Spirit", 250, Color(0xFF81C784)),
    ShopItem("skin_purple", "Void Walker", 500, Color(0xFFBA68C8)),
    ShopItem("skin_gold", "Golden God", 1000, Color(0xFFFFD700)),
    ShopItem("skin_ninja", "Ninja Mode", 2000, Color.DarkGray)
)