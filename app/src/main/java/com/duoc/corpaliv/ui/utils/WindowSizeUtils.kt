package com.duoc.corpaliv.ui.utils

// provee el tamano de pantalla calculado una sola vez desde mainactivity
// evita recalcular metricas de ventana en cada recomposicion

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWindowWidthSizeClass = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
