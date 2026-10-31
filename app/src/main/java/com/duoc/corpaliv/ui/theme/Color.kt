package com.duoc.corpaliv.ui.theme

// colores oficiales de la app con soporte para modo claro y modo oscuro
// define los fondos, textos, bordes y colores institucionales como naranja y morado

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// grupo de colores necesarios para pintar una pantalla completa
data class AlbaPalette(
    val fondo: Color,
    val tarjeta: Color,
    val borde: Color,
    val textoPrincipal: Color,
    val textoSecundario: Color,
    val superficie: Color,
    val esOscuro: Boolean
)

// combinacion de colores para el modo oscuro con fondo negro
val PaletaOscura = AlbaPalette(
    fondo = Color(0xFF0B0B0B),
    tarjeta = Color(0xFF161616),
    borde = Color(0xFF262626),
    textoPrincipal = Color(0xFFFFFFFF),
    textoSecundario = Color(0xFF9E9E9E),
    superficie = Color(0xFF121212),
    esOscuro = true
)

// combinacion de colores para el modo claro con fondo crema institucional
val PaletaClara = AlbaPalette(
    fondo = Color(0xFFFAF8F5),
    tarjeta = Color(0xFFFFFFFF),
    borde = Color(0xFFD0CCC2),
    textoPrincipal = Color(0xFF121212),
    textoSecundario = Color(0xFF6E6C68),
    superficie = Color(0xFFF2EFEA),
    esOscuro = false
)

// variable global de compose que entrega la paleta de colores activa
val LocalAlbaPalette = compositionLocalOf { PaletaClara }

// variable booleana que avisa a toda la app si el modo oscuro esta prendido
val LocalEsTemaOscuro = compositionLocalOf { false }

// funcion para cambiar de tema claro a oscuro desde cualquier boton
val LocalAlternarTema = compositionLocalOf<() -> Unit> { {} }

// variables dinamicas para que las pantallas tomen el color correcto automaticamente
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

// colores fijos de corpaliv que no cambian: naranja, morado, rojo y verde
val AlbaOrange = Color(0xFFEA6A12)
val AlbaPurple = Color(0xFF5E4FA7)
val AlbaError = Color(0xFFFF5252)
val AlbaGreen = Color(0xFF4CAF50)
