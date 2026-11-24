package com.duoc.corpaliv.ui.theme

// formas y bordes redondeados unificados para toda la aplicacion
// mantiene coherencia visual entre tarjetas, botones, campos y etiquetas

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// radios estandar usados en los componentes de corpaliv
object AlbaShapes {
    val tarjeta = RoundedCornerShape(16.dp)
    val boton = RoundedCornerShape(12.dp)
    val campo = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(50)
    val dialogo = RoundedCornerShape(20.dp)
    val drawer = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    val barraFlotante = RoundedCornerShape(28.dp)
}
