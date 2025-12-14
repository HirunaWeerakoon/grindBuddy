package com.example.grindbuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.grindbuddy.R


enum class MascotMood {
    HAPPY,
    FOCUSED
}

@Composable
fun GrindMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier
) {
    // Select image based on mood
    val imageRes = when (mood) {
        MascotMood.HAPPY -> R.drawable.mascot_happy
        MascotMood.FOCUSED -> R.drawable.mascot_focused
    }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Grind Buddy",
            modifier = Modifier.size(120.dp) // Good size for a buddy
        )
    }
}