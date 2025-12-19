package com.example.grindbuddy.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindbuddy.data.SHOP_ITEMS
import com.example.grindbuddy.data.ShopItem
import com.example.grindbuddy.presentation.theme.PrimaryPurple

@Composable
fun ShopScreen(
    coins: Int,
    ownedItems: Set<String>,
    equippedId: String,
    onBuyClick: (ShopItem) -> Unit,
    onEquipClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // --- TOP BAR ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onBackClick) { Text("<") }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("SHOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Balance: $coins 💰", color = Color.Yellow, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- GRID OF ITEMS ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Columns
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SHOP_ITEMS) { item ->
                ShopItemCard(
                    item = item,
                    isOwned = ownedItems.contains(item.id),
                    isEquipped = equippedId == item.id,
                    canAfford = coins >= item.price,
                    onBuy = { onBuyClick(item) },
                    onEquip = { onEquipClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    val borderColor = if (isEquipped) Color.Yellow else Color.Transparent
    val borderWidth = if (isEquipped) 2.dp else 0.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. PREVIEW CIRCLE (The "Skin")
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(item.color.takeIf { it != Color.Unspecified } ?: Color.White)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. NAME
            Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // 3. ACTION BUTTON
            if (isOwned) {
                if (isEquipped) {
                    Text("EQUIPPED", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("EQUIP", fontSize = 12.sp)
                    }
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) Color(0xFFD4AF37) else Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("${item.price} 💰", fontSize = 12.sp, color = Color.Black)
                }
            }
        }
    }
}