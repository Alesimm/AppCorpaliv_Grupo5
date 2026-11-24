package com.duoc.corpaliv.ui.components

// micro-interacciones fieles a los videos de referencia adaptadas a la paleta corpaliv
// incluye liquid toggle, heart burst con mini corazones, pill dots reactivos, bouncing dots y skeleton

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// interruptor elastico tipo gota liquida: se estira al deslizarse y se comprime al llegar al borde
@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colorActivo: Color = AlbaOrange,
    ancho: Dp = 52.dp,
    alto: Dp = 28.dp
) {
    val esOscuro = LocalEsTemaOscuro.current
    val progreso = remember { Animatable(if (checked) 1f else 0f) }

    LaunchedEffect(checked) {
        progreso.animateTo(
            targetValue = if (checked) 1f else 0f,
            animationSpec = spring(
                dampingRatio = 0.58f,
                stiffness = 360f
            )
        )
    }

    val colorPistaInactiva = if (esOscuro) Color(0xFF2B2B2B) else Color(0xFFD9D4CA)
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

        // pista redondeada con ligero brillo interior cuando esta encendida
        drawRoundRect(
            color = colorPista,
            cornerRadius = CornerRadius(radioPista, radioPista)
        )

        // efecto gota: se estira al medio del recorrido y hace ligero squish al chocar con el borde
        val estiramientoMedio = sin(t * PI).toFloat().coerceAtLeast(0f)
        val squishBorde = (abs(progreso.value - t) * 2.2f).coerceIn(0f, 0.16f)

        val anchoPerilla = diametroBase * (1f + 0.38f * estiramientoMedio - squishBorde)
        val altoPerilla = diametroBase * (1f - 0.10f * estiramientoMedio + squishBorde * 0.7f)

        val recorrido = (size.width - (margen * 2f) - anchoPerilla).coerceAtLeast(0f)
        val posX = margen + (recorrido * t)
        val posY = (size.height - altoPerilla) / 2f

        // sombra suave bajo la perilla
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(posX, posY + 1.2.dp.toPx()),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )

        // perilla blanca elastica
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(posX, posY),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )
    }
}

// dibuja un mini corazon vectorial centrado en el origen para la explosion de favoritos
private fun DrawScope.dibujarMiniCorazon(
    path: Path,
    centro: Offset,
    tamano: Float,
    color: Color
) {
    path.reset()
    val s = tamano / 2f
    path.moveTo(0f, s * 0.65f)
    path.cubicTo(-s * 1.1f, -s * 0.15f, -s * 0.65f, -s * 1.05f, 0f, -s * 0.35f)
    path.cubicTo(s * 0.65f, -s * 1.05f, s * 1.1f, -s * 0.15f, 0f, s * 0.65f)
    path.close()

    withTransform({
        translate(left = centro.x, top = centro.y)
    }) {
        drawPath(path = path, color = color)
    }
}

// boton de corazon fiel al video: rebota relleno con anillo expansivo y mini corazones
@Composable
fun BotonFavoritoBurst(
    esFavorito: Boolean,
    alAlternar: () -> Unit,
    modifier: Modifier = Modifier,
    tamanoIcono: Dp = 20.dp
) {
    val escalaCorazon = remember { Animatable(1f) }
    val progresoBurst = remember { Animatable(1f) }
    val miniCorazonPath = remember { Path() }
    var animacionActiva by remember { mutableStateOf(false) }
    var estadoPrevio by remember { mutableStateOf(esFavorito) }

    LaunchedEffect(esFavorito) {
        if (esFavorito && !estadoPrevio) {
            animacionActiva = true
            progresoBurst.snapTo(0f)
            escalaCorazon.snapTo(0.55f)
            escalaCorazon.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.45f,
                    stiffness = Spring.StiffnessMedium
                )
            )
            progresoBurst.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 480, easing = EaseOutCubic)
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
            .size(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { alAlternar() },
        contentAlignment = Alignment.Center
    ) {
        // anillo y 6 mini corazones saliendo hacia afuera en naranja y morado corpaliv
        if (animacionActiva && progresoBurst.value < 1f) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val p = progresoBurst.value
                val alpha = (1f - p).coerceIn(0f, 1f)
                val centro = Offset(size.width / 2f, size.height / 2f)
                val radioMaximo = size.minDimension * 0.48f

                // anillo circular que se expande y se desvanece
                drawCircle(
                    color = AlbaOrange.copy(alpha = alpha * 0.65f),
                    radius = radioMaximo * (0.30f + 0.70f * p),
                    center = centro,
                    style = Stroke(width = (2.4.dp.toPx() * alpha).coerceAtLeast(0.5f))
                )

                // mini corazones explotando en 6 direcciones
                val cantidad = 6
                for (i in 0 until cantidad) {
                    val angulo = ((i * 60.0) - 30.0) * (PI / 180.0)
                    val distancia = radioMaximo * (0.38f + 0.65f * p)
                    val px = centro.x + (cos(angulo) * distancia).toFloat()
                    val py = centro.y + (sin(angulo) * distancia).toFloat()
                    val colorMini = if (i % 2 == 0) AlbaOrange else AlbaPurple
                    val tamanoMini = (8.dp.toPx() * (0.55f + 0.45f * (1f - p))).coerceAtLeast(2f)

                    dibujarMiniCorazon(
                        path = miniCorazonPath,
                        centro = Offset(px, py),
                        tamano = tamanoMini,
                        color = colorMini.copy(alpha = alpha)
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

// indicador de paginas fiel al video: el punto activo se estira en pildora en tiempo real al deslizar
@Composable
fun PillDotsIndicator(
    cantidad: Int,
    paginaActual: Int,
    fraccionDesplazamiento: Float = 0f,
    colorActivo: Color = AlbaOrange,
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current
    val colorInactivo = if (esOscuro) Color(0xFF3A3A3A) else Color(0xFFCFCAC0)
    val posicionContinua = (paginaActual + fraccionDesplazamiento).coerceIn(0f, (cantidad - 1).coerceAtLeast(0).toFloat())

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until cantidad) {
            val distancia = abs(posicionContinua - i).coerceIn(0f, 1f)
            val seleccion = 1f - distancia
            val anchoPunto = lerp(8.dp, 28.dp, seleccion)
            val colorPunto = androidx.compose.ui.graphics.lerp(colorInactivo, colorActivo, seleccion)

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

// 3 puntos rebotando en ola (0.14s de desfase, ciclo 0.9s) con squash suave al aterrizar
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
            val faseLocal = (fase - (i * 0.155f) + 1f) % 1f
            val salto = if (faseLocal < 0.5f) {
                sin((faseLocal / 0.5f) * PI).toFloat()
            } else {
                0f
            }
            val compresion = if (faseLocal in 0.48f..0.62f) 0.15f else 0f

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

// tarjeta skeleton que pulsa suavemente cada 1.6s para cargas de red y listas
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(AlbaShapes.boton)
                .background(base)
        )
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
