package com.duoc.corpaliv.ui.theme

// configuracion del tema global de la aplicacion con transicion animada de colores
// se conecta con color.kt, type.kt y mainactivity

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun AppCorpaliv_Grupo5Theme(
    esTemaOscuro: Boolean = false,
    alAlternarTema: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val duracion = 350

    val fondoAnimado by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.fondo else PaletaClara.fondo,
        animationSpec = tween(duracion),
        label = "animFondo"
    )
    val tarjetaAnimada by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.tarjeta else PaletaClara.tarjeta,
        animationSpec = tween(duracion),
        label = "animTarjeta"
    )
    val bordeAnimado by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.borde else PaletaClara.borde,
        animationSpec = tween(duracion),
        label = "animBorde"
    )
    val textoPrincipalAnimado by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.textoPrincipal else PaletaClara.textoPrincipal,
        animationSpec = tween(duracion),
        label = "animTextoPrincipal"
    )
    val textoSecundarioAnimado by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.textoSecundario else PaletaClara.textoSecundario,
        animationSpec = tween(duracion),
        label = "animTextoSecundario"
    )
    val superficieAnimada by animateColorAsState(
        targetValue = if (esTemaOscuro) PaletaOscura.superficie else PaletaClara.superficie,
        animationSpec = tween(duracion),
        label = "animSuperficie"
    )

    val paletaActiva = AlbaPalette(
        fondo = fondoAnimado,
        tarjeta = tarjetaAnimada,
        borde = bordeAnimado,
        textoPrincipal = textoPrincipalAnimado,
        textoSecundario = textoSecundarioAnimado,
        superficie = superficieAnimada,
        esOscuro = esTemaOscuro
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !esTemaOscuro
                insetsController.isAppearanceLightNavigationBars = !esTemaOscuro
            }
        }
    }

    val colorScheme = if (esTemaOscuro) {
        darkColorScheme(
            primary = AlbaOrange,
            onPrimary = Color.White,
            secondary = textoPrincipalAnimado,
            onSecondary = fondoAnimado,
            background = fondoAnimado,
            onBackground = textoPrincipalAnimado,
            surface = tarjetaAnimada,
            onSurface = textoPrincipalAnimado,
            surfaceVariant = tarjetaAnimada,
            onSurfaceVariant = textoSecundarioAnimado,
            error = AlbaError
        )
    } else {
        lightColorScheme(
            primary = AlbaOrange,
            onPrimary = Color.White,
            secondary = textoPrincipalAnimado,
            onSecondary = Color.White,
            background = fondoAnimado,
            onBackground = textoPrincipalAnimado,
            surface = tarjetaAnimada,
            onSurface = textoPrincipalAnimado,
            surfaceVariant = superficieAnimada,
            onSurfaceVariant = textoSecundarioAnimado,
            error = AlbaError
        )
    }

    CompositionLocalProvider(
        LocalAlbaPalette provides paletaActiva,
        LocalEsTemaOscuro provides esTemaOscuro,
        LocalAlternarTema provides alAlternarTema
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
