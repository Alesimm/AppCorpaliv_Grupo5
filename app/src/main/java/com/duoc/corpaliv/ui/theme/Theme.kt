package com.duoc.corpaliv.ui.theme

// aplica el tema visual con renderizado directo a pantalla para maximos fps
// solo activa la grabacion de graphicsLayer durante 1 frame al alternar modo claro y oscuro

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import kotlin.math.sin

// envuelve todas las pantallas y aplica el efecto cortina curvo sin penalizar fps en reposo
@Composable
fun AppCorpaliv_Grupo5Theme(
    esTemaOscuro: Boolean = false,
    alAlternarTema: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var capturaAnterior by remember { mutableStateOf<ImageBitmap?>(null) }
    val progresoCortina = remember { Animatable(1f) }
    var grabandoSnapshot by remember { mutableStateOf(false) }
    var animandoTema by remember { mutableStateOf(false) }

    // graba un solo frame antes de alternar el tema para no gastar gpu en cada cuadro normal
    val alternarTemaConCortina: () -> Unit = remember(alAlternarTema) {
        {
            if (!animandoTema) {
                scope.launch {
                    animandoTema = true
                    try {
                        grabandoSnapshot = true
                        // espera un frame para que graphicsLayer capture la vista actual
                        withFrameNanos { }
                        capturaAnterior = graphicsLayer.toImageBitmap()
                        grabandoSnapshot = false
                        progresoCortina.snapTo(0f)
                        alAlternarTema()
                        progresoCortina.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 480, easing = EaseInOutCubic)
                        )
                    } catch (_: Exception) {
                        grabandoSnapshot = false
                        alAlternarTema()
                    } finally {
                        capturaAnterior = null
                        grabandoSnapshot = false
                        animandoTema = false
                    }
                }
            }
        }
    }

    val paletaActiva = if (esTemaOscuro) PaletaOscura else PaletaClara

    // pinta la ventana del sistema y actualiza iconos de estado para eliminar destellos blancos
    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(esTemaOscuro) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.decorView.setBackgroundColor(paletaActiva.fondo.toArgb())
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !esTemaOscuro
                insetsController.isAppearanceLightNavigationBars = !esTemaOscuro
            }
        }
    }

    val colorScheme = remember(esTemaOscuro) {
        if (esTemaOscuro) {
            darkColorScheme(
                primary = AlbaOrange,
                onPrimary = Color.White,
                secondary = PaletaOscura.textoPrincipal,
                onSecondary = PaletaOscura.fondo,
                background = PaletaOscura.fondo,
                onBackground = PaletaOscura.textoPrincipal,
                surface = PaletaOscura.tarjeta,
                onSurface = PaletaOscura.textoPrincipal,
                surfaceVariant = PaletaOscura.tarjeta,
                onSurfaceVariant = PaletaOscura.textoSecundario,
                error = AlbaError
            )
        } else {
            lightColorScheme(
                primary = AlbaOrange,
                onPrimary = Color.White,
                secondary = PaletaClara.textoPrincipal,
                onSecondary = Color.White,
                background = PaletaClara.fondo,
                onBackground = PaletaClara.textoPrincipal,
                surface = PaletaClara.tarjeta,
                onSurface = PaletaClara.textoPrincipal,
                surfaceVariant = PaletaClara.superficie,
                onSurfaceVariant = PaletaClara.textoSecundario,
                error = AlbaError
            )
        }
    }

    CompositionLocalProvider(
        LocalAlbaPalette provides paletaActiva,
        LocalEsTemaOscuro provides esTemaOscuro,
        LocalAlternarTema provides alternarTemaConCortina
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // renderizado directo a pantalla en uso normal; solo usa graphicsLayer durante la captura
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            if (grabandoSnapshot) {
                                graphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            } else {
                                drawContent()
                            }
                        }
                ) {
                    content()
                }

                val snapshot = capturaAnterior
                if (snapshot != null && progresoCortina.value < 1f) {
                    val pathCortina = remember { Path() }
                    val pathBorde = remember { Path() }
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val t = progresoCortina.value
                        val curvaturaMax = 96.dp.toPx()
                        val arco = sin(t * Math.PI).toFloat() * curvaturaMax
                        val baseY = (size.height + curvaturaMax) * t - (arco * 0.35f)

                        pathCortina.reset()
                        pathCortina.moveTo(0f, baseY)
                        pathCortina.quadraticTo(
                            size.width * 0.5f,
                            baseY + arco,
                            size.width,
                            baseY
                        )
                        pathCortina.lineTo(size.width, size.height)
                        pathCortina.lineTo(0f, size.height)
                        pathCortina.close()

                        clipPath(path = pathCortina) {
                            drawImage(image = snapshot)
                        }

                        pathBorde.reset()
                        pathBorde.moveTo(0f, baseY)
                        pathBorde.quadraticTo(
                            size.width * 0.5f,
                            baseY + arco,
                            size.width,
                            baseY
                        )
                        drawPath(
                            path = pathBorde,
                            color = AlbaOrange.copy(alpha = (1f - t) * 0.75f),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}
