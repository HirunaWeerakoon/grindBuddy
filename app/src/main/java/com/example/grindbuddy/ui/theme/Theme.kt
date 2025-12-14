package com.example.grindbuddy.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.*
import androidx.core.view.WindowCompat
import com.example.grindbuddy.presentation.theme.*

private val DarkColorScheme = darkColorScheme(
    primary = GrindGreen,
    secondary = GrindAccent,
    background = GrindBlack,
    surface = GrindSurface,
    onPrimary = GrindBlack,
    onSecondary = GrindWhite,
    onBackground = GrindWhite,
    onSurface = GrindWhite,
)

@Composable
fun GrindBuddyTheme(

    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set Status Bar to our Dark Background
            window.statusBarColor = GrindBlack.toArgb()
            // Tell system: "Icons should be light" (because background is dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}