package com.duoc.corpaliv.ui.theme

// colores oficiales de la app con soporte para modo claro y modo oscuro
// define fondos, superficies, bordes, botones y acentos adaptados para cada tema

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// grupo de colores necesarios para pintar una pantalla completa
@Immutable
data class AlbaPalette(
    val fondo: Color,
    val tarjeta: Color,
    val borde: Color,
    val textoPrincipal: Color,
    val textoSecundario: Color,
    val superficie: Color,
    val acento: Color,
    val botonPrincipalFondo: Color,
    val botonPrincipalTexto: Color,
    val esOscuro: Boolean
)

// modo oscuro en gris carbon elegante (sin hundirse a negro puro al abrir ventanas)
val PaletaOscura = AlbaPalette(
    fondo = Color(0xFF1A1A1E),
    tarjeta = Color(0xFF26262C),
    borde = Color(0xFF383842),
    textoPrincipal = Color(0xFFF4F4F5),
    textoSecundario = Color(0xFFA8A8B3),
    superficie = Color(0xFF202025),
    acento = Color(0xFFF09352),
    botonPrincipalFondo = Color(0xFF34343D),
    botonPrincipalTexto = Color(0xFFF4F4F5),
    esOscuro = true
)

// modo claro en crema editorial con acento naranja institucional
val PaletaClara = AlbaPalette(
    fondo = Color(0xFFFAF8F5),
    tarjeta = Color(0xFFFFFFFF),
    borde = Color(0xFFD8D4CA),
    textoPrincipal = Color(0xFF141416),
    textoSecundario = Color(0xFF6B6A66),
    superficie = Color(0xFFF3F0EA),
    acento = Color(0xFFEA6A12),
    botonPrincipalFondo = Color(0xFF141416),
    botonPrincipalTexto = Color(0xFFFFFFFF),
    esOscuro = false
)

// variable global de compose que entrega la paleta de colores activa
val LocalAlbaPalette = compositionLocalOf { PaletaClara }

// variable booleana que avisa a toda la app si el modo oscuro esta prendido
val LocalEsTemaOscuro = compositionLocalOf { false }

// funcion para cambiar de tema claro a oscuro desde cualquier boton
val LocalAlternarTema = staticCompositionLocalOf<() -> Unit> { {} }

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

// acento principal que suaviza el naranja a tono ambar elegante en modo oscuro
val AlbaOrange: Color
    @Composable
    get() = LocalAlbaPalette.current.acento

// colores para botones principales (sutil en modo oscuro, alto contraste en modo claro)
val AlbaButtonBg: Color
    @Composable
    get() = LocalAlbaPalette.current.botonPrincipalFondo

val AlbaButtonText: Color
    @Composable
    get() = LocalAlbaPalette.current.botonPrincipalTexto

// colores secundarios fijos de estado y marca
val AlbaPurple = Color(0xFF6F61BA)
val AlbaError = Color(0xFFE85D5D)
val AlbaGreen = Color(0xFF4CAF6A)
