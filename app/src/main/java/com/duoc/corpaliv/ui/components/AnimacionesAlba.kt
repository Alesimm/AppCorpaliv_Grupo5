package com.duoc.corpaliv.ui.components

// micro-interacciones y componentes animados reutilizables con la paleta corpaliv
// incluye liquid toggle, heart burst, pill dots, bouncing dots y skeleton pulse

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// interruptor elastico tipo gota liquida que se estira al deslizarse y rebota al llegar
@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colorActivo: Color = AlbaOrange,
    ancho: Dp = 54.dp,
    alto: Dp = 30.dp
) {
    val esOscuro = LocalEsTemaOscuro.current
    val progreso = remember { Animatable(if (checked) 1f else 0f) }

    LaunchedEffect(checked) {
        progreso.animateTo(
            targetValue = if (checked) 1f else 0f,
            animationSpec = spring(
                dampingRatio = 0.62f,
                stiffness = 380f
            )
        )
    }

    val colorPistaInactiva = if (esOscuro) Color(0xFF2B2B2B) else Color(0xFFDAD5CB)
    val colorPista by animateColorAsState(
        targetValue = if (checked) colorActivo else colorPistaInactiva,
        animationSpec = tween(durationMillis = 240),
        label = "colorPistaToggle"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Canvas(
        modifier = modifier
            .size(width = ancho, height = alto)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        val t = progreso.value.coerceIn(0f, 1f)
        val radioPista = size.height / 2f
        val margen = 3.dp.toPx()
        val diametroBase = size.height - (margen * 2f)

        // dibuja la pista redondeada de fondo
        drawRoundRect(
            color = colorPista,
            cornerRadius = CornerRadius(radioPista, radioPista)
        )

        // la perilla se estira en horizontal a mitad de camino y se compacta al llegar al borde
        val factorEstiramiento = sin(t * PI).toFloat().coerceAtLeast(0f)
        val anchoPerilla = diametroBase + (diametroBase * 0.34f * factorEstiramiento)
        val altoPerilla = diametroBase - (diametroBase * 0.08f * factorEstiramiento)

        val recorridoDisponible = (size.width - (margen * 2f) - anchoPerilla).coerceAtLeast(0f)
        val posX = margen + (recorridoDisponible * t)
        val posY = (size.height - altoPerilla) / 2f

        // sombra sutil debajo de la perilla para dar profundidad
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.16f),
            topLeft = Offset(posX, posY + 1.5.dp.toPx()),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )

        // perilla principal blanca
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(posX, posY),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )
    }
}

// boton de corazon con explosion de anillo y particulas al marcar favorito
@Composable
fun BotonFavoritoBurst(
    esFavorito: Boolean,
    alAlternar: () -> Unit,
    modifier: Modifier = Modifier,
    tamanoIcono: Dp = 20.dp
) {
    val escalaCorazon = remember { Animatable(1f) }
    val progresoBurst = remember { Animatable(1f) }
    var animacionActiva by remember { mutableStateOf(false) }
    var estadoPrevio by remember { mutableStateOf(esFavorito) }

    // dispara la explosion solo cuando el usuario pasa de no-favorito a favorito
    LaunchedEffect(esFavorito) {
        if (esFavorito && !estadoPrevio) {
            animacionActiva = true
            progresoBurst.snapTo(0f)
            escalaCorazon.snapTo(0.65f)
            escalaCorazon.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            progresoBurst.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 420, easing = EaseOutCubic)
            )
            animacionActiva = false
        }
        estadoPrevio = esFavorito
    }

    val colorCorazon by animateColorAsState(
        targetValue = if (esFavorito) AlbaOrange else AlbaTextSecondary,
        animationSpec = tween(durationMillis = 220),
        label = "colorCorazonBurst"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { alAlternar() },
        contentAlignment = Alignment.Center
    ) {
        // anillo expansivo y 8 chispas en colores corpaliv que explotan hacia afuera
        if (animacionActiva && progresoBurst.value < 1f) {
            Canvas(modifier = Modifier.size(44.dp)) {
                val p = progresoBurst.value
                val alpha = (1f - p).coerceIn(0f, 1f)
                val centro = Offset(size.width / 2f, size.height / 2f)
                val radioMaximo = size.minDimension * 0.48f

                // anillo de onda expansiva
                drawCircle(
                    color = AlbaOrange.copy(alpha = alpha * 0.55f),
                    radius = radioMaximo * (0.35f + 0.65f * p),
                    center = centro,
                    style = Stroke(width = (2.5.dp.toPx() * alpha).coerceAtLeast(0.5f))
                )

                // particulas alternadas naranja y morado
                val cantidad = 8
                for (i in 0 until cantidad) {
                    val angulo = (i * (360.0 / cantidad)) * (PI / 180.0)
                    val distancia = radioMaximo * (0.4f + 0.65f * p)
                    val px = centro.x + (cos(angulo) * distancia).toFloat()
                    val py = centro.y + (sin(angulo) * distancia).toFloat()
                    val colorParticula = if (i % 2 == 0) AlbaOrange else AlbaPurple
                    val radioParticula = (3.2.dp.toPx() * alpha).coerceAtLeast(0.5f)
                    drawCircle(
                        color = colorParticula.copy(alpha = alpha),
                        radius = radioParticula,
                        center = Offset(px, py)
                    )
                }
            }
        }

        Icon(
            imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (esFavorito) "Quitar de favoritos" else "Guardar en favoritos",
            tint = colorCorazon,
            modifier = Modifier
                .size(tamanoIcono)
                .graphicsLayer {
                    scaleX = escalaCorazon.value
                    scaleY = escalaCorazon.value
                }
        )
    }
}

// indicador de paginas donde el punto activo se estira suavemente en forma de pildora
@Composable
fun PillDotsIndicator(
    cantidad: Int,
    paginaActual: Int,
    colorActivo: Color = AlbaOrange,
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current
    val colorInactivo = if (esOscuro) Color(0xFF3A3A3A) else Color(0xFFCFCAC0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until cantidad) {
            val activo = i == paginaActual
            val anchoPunto by animateDpAsState(
                targetValue = if (activo) 26.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = 400f
                ),
                label = "anchoPillDot"
            )
            val colorPunto by animateColorAsState(
                targetValue = if (activo) colorActivo else colorInactivo,
                animationSpec = tween(durationMillis = 240),
                label = "colorPillDot"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(anchoPunto)
                    .clip(CircleShape)
                    .background(colorPunto)
            )
        }
    }
}

// indicador de carga con tres puntos rebotando en ola y ligera compresion al caer
@Composable
fun BouncingDotsLoader(
    modifier: Modifier = Modifier,
    color: Color = AlbaOrange,
    tamanoPunto: Dp = 9.dp
) {
    val transicion = rememberInfiniteTransition(label = "bouncingDots")
    val fase by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "faseBouncing"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            // desfasa cada punto en 0.16 del ciclo (~140ms)
            val faseLocal = (fase - (i * 0.16f) + 1f) % 1f
            val salto = if (faseLocal < 0.5f) {
                sin((faseLocal / 0.5f) * PI).toFloat()
            } else {
                0f
            }
            // ligero squash al aterrizar
            val compresion = if (faseLocal in 0.48f..0.62f) 0.14f else 0f

            Box(
                modifier = Modifier
                    .size(tamanoPunto)
                    .graphicsLayer {
                        translationY = -salto * 10.dp.toPx()
                        scaleX = 1f + compresion
                        scaleY = 1f - compresion
                    }
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// tarjeta placeholder con pulso suave cada 1.6s lista para cargas de red y listas
@Composable
fun SkeletonCardPulse(
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current
    val base = if (esOscuro) Color(0xFF262626) else Color(0xFFE6E2D8)

    val transicion = rememberInfiniteTransition(label = "skeletonPulse")
    val opacidad by transicion.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaSkeleton"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AlbaShapes.tarjeta)
            .background(AlbaCard)
            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
            .padding(16.dp)
            .graphicsLayer { alpha = opacidad },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // bloque superior simulando imagen o encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(AlbaShapes.boton)
                .background(base)
        )
        // tres lineas de texto simuladas
        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(14.dp)
                .clip(AlbaShapes.chip)
                .background(base)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(11.dp)
                .clip(AlbaShapes.chip)
                .background(base)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(11.dp)
                .clip(AlbaShapes.chip)
                .background(base)
        )
    }
}
