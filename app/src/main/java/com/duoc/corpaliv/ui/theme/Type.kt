package com.duoc.corpaliv.ui.theme

// estilo y tamano de las letras para toda la aplicacion
// define el tipo de letra normal para que los textos sean comodos de leer

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// tamano base de letra de 16 sp usado en los parrafos de texto
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
