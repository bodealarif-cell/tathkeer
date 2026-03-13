package com.example.tathkeer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// الألوان الفاتحة
val Green50 = Color(0xFFE8F5E9)
val Green100 = Color(0xFFC8E6C9)
val Green200 = Color(0xFFA5D6A7)
val Green300 = Color(0xFF81C784)
val Green400 = Color(0xFF66BB6A)
val Green500 = Color(0xFF4CAF50)
val Green600 = Color(0xFF43A047)
val Green700 = Color(0xFF388E3C)
val Green800 = Color(0xFF2E7D32)
val Green900 = Color(0xFF1B5E20)

// الألوان الداكنة
val DarkGreen = Color(0xFF1F3A1F)
val DarkSurface = Color(0xFF1E2A1E)

@Composable
fun TathkeerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) {
        lightColors(
            primary = Green600,
            onPrimary = Color.White,
            primaryContainer = Green100,
            onPrimaryContainer = Green900,
            secondary = Green400,
            onSecondary = Color.Black,
            background = Green50,
            surface = Color.White,
            onSurface = Green900,
            surfaceVariant = Green100
        )
    } else {
        darkColors(
            primary = Green400,
            onPrimary = Color.Black,
            primaryContainer = Green800,
            onPrimaryContainer = Green100,
            secondary = Green600,
            onSecondary = Color.White,
            background = DarkGreen,
            surface = DarkSurface,
            onSurface = Green100,
            surfaceVariant = Green800
        )
    }

    Material3(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
