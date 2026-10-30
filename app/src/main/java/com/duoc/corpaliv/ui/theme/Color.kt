package com.duoc.corpaliv.ui.theme

// paleta de colores reactiva con soporte para modo oscuro y modo claro
// se conecta con theme.kt y con todos los componentes y pantallas de la interfaz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// estructura de datos inmutable que agrupa los colores del tema visual
data class AlbaPalette(
    val fondo: Color,
    val tarjeta: Color,
    val borde: Color,
    val textoPrincipal: Color,
    val textoSecundario: Color,
    val superficie: Color,
    val esOscuro: Boolean
)

// paleta de colores para el modo oscuro original
val PaletaOscura = AlbaPalette(
    fondo = Color(0xFF0B0B0B),
    tarjeta = Color(0xFF161616),
    borde = Color(0xFF262626),
    textoPrincipal = Color(0xFFFFFFFF),
    textoSecundario = Color(0xFF9E9E9E),
    superficie = Color(0xFF121212),
    esOscuro = true
)

// paleta de colores para el modo claro basada exactamente en el mockup oficial
val PaletaClara = AlbaPalette(
    fondo = Color(0xFFFAF8F5),
    tarjeta = Color(0xFFFFFFFF),
    borde = Color(0xFFD0CCC2),
    textoPrincipal = Color(0xFF121212),
    textoSecundario = Color(0xFF6E6C68),
    superficie = Color(0xFFF2EFEA),
    esOscuro = false
)

// proveedor de composicion para la paleta activa en la jerarquia de vistas (por defecto clara)
val LocalAlbaPalette = compositionLocalOf { PaletaClara }

// proveedor de composicion booleano que indica si el modo oscuro esta activo (por defecto false)
val LocalEsTemaOscuro = compositionLocalOf { false }

// proveedor de composicion para invocar la alternancia de tema desde cualquier vista
val LocalAlternarTema = compositionLocalOf<() -> Unit> { {} }

// accesores reactivos dinamicos que permiten a las pantallas adaptarse sin tocar su estructura
val AlbaBlack: Color
    @Composable
    get() = LocalAlbaPalette.current.fondo

val AlbaSurface: Color
    @Composable
    get() = LocalAlbaPalette.current.superficie

val AlbaCard: Color
    @Composable
    get() = LocalAlbaPalette.current.tarjeta

val AlbaBorder: Color
    @Composable
    get() = LocalAlbaPalette.current.borde

val AlbaWhite: Color
    @Composable
    get() = LocalAlbaPalette.current.textoPrincipal

val AlbaTextSecondary: Color
    @Composable
    get() = LocalAlbaPalette.current.textoSecundario

// colores de identidad institucional fijos comunes a ambos temas
val AlbaOrange = Color(0xFFEA6A12)
val AlbaPurple = Color(0xFF5E4FA7)
val AlbaError = Color(0xFFFF5252)
val AlbaGreen = Color(0xFF4CAF50)
