package com.duoc.corpaliv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// esquema de color oscuro fijo para mantener la identidad visual
private val AlbaDarkColorScheme = darkColorScheme(
    primary = AlbaOrange,
    onPrimary = AlbaWhite,
    secondary = AlbaWhite,
    onSecondary = AlbaBlack,
    background = AlbaBlack,
    onBackground = AlbaWhite,
    surface = AlbaSurface,
    onSurface = AlbaWhite,
    surfaceVariant = AlbaCard,
    onSurfaceVariant = AlbaTextSecondary,
    error = AlbaError
)

@Composable
fun AppCorpaliv_Grupo5Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AlbaDarkColorScheme,
        typography = Typography,
        content = content
    )
}