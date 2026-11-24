package com.duoc.corpaliv.ui.components

// micro-interacciones y componentes animados reutilizables con la paleta corpaliv
// incluye liquid toggle, heart burst, pill dots, bouncing dots, skeletons, dialogo animado y bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

        drawRoundRect(
            color = colorPista,
            cornerRadius = CornerRadius(radioPista, radioPista)
        )

        val estiramientoMedio = sin(t * PI).toFloat().coerceAtLeast(0f)
        val squishBorde = (abs(progreso.value - t) * 2.2f).coerceIn(0f, 0.16f)

        val anchoPerilla = diametroBase * (1f + 0.38f * estiramientoMedio - squishBorde)
        val altoPerilla = diametroBase * (1f - 0.10f * estiramientoMedio + squishBorde * 0.7f)

        val recorrido = (size.width - (margen * 2f) - anchoPerilla).coerceAtLeast(0f)
        val posX = margen + (recorrido * t)
        val posY = (size.height - altoPerilla) / 2f

        drawRoundRect(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(posX, posY + 1.2.dp.toPx()),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(posX, posY),
            size = Size(anchoPerilla, altoPerilla),
            cornerRadius = CornerRadius(altoPerilla / 2f, altoPerilla / 2f)
        )
    }
}

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

// boton de corazon con anillo expansivo y 6 mini corazones en naranja y morado corpaliv
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
            escalaCorazon.snapTo(0.60f)
            // rebote del corazon y explosion de mini corazones en paralelo para respuesta instantanea
            coroutineScope {
                launch {
                    escalaCorazon.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = 0.45f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
                launch {
                    progresoBurst.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 300, easing = EaseOutCubic)
                    )
                }
            }
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
        if (animacionActiva && progresoBurst.value < 1f) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val p = progresoBurst.value
                val alpha = (1f - p).coerceIn(0f, 1f)
                val centro = Offset(size.width / 2f, size.height / 2f)
                val radioMaximo = size.minDimension * 0.48f

                drawCircle(
                    color = AlbaOrange.copy(alpha = alpha * 0.65f),
                    radius = radioMaximo * (0.30f + 0.70f * p),
                    center = centro,
                    style = Stroke(width = (2.4.dp.toPx() * alpha).coerceAtLeast(0.5f))
                )

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

// indicador de paginas donde el punto activo se estira en pildora en tiempo real al deslizar
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

// tarjeta skeleton generica que pulsa suavemente cada 1.6s para cargas de red y listas
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

// skeleton 1 a 1 que replica fielmente la estructura real del comprobante mientras se registra
@Composable
fun SkeletonComprobantePulse(
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current
    val base = if (esOscuro) Color(0xFF262626) else Color(0xFFE6E2D8)

    val transicion = rememberInfiniteTransition(label = "skeletonComprobante")
    val opacidad by transicion.animateFloat(
        initialValue = 0.42f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 760, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaSkeletonComp"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .graphicsLayer { alpha = opacidad }
    ) {
        // cabecera simulando el circulo de check y el titulo "Solicitud enviada"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(base)
            )
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(24.dp)
                    .clip(AlbaShapes.chip)
                    .background(base)
            )
        }

        // dos lineas simulando el subtitulo descriptivo
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(12.dp)
                .clip(AlbaShapes.chip)
                .background(base)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(12.dp)
                .clip(AlbaShapes.chip)
                .background(base)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // tarjeta simulada del folio correlativo y etiqueta de estado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AlbaShapes.tarjeta)
                .background(AlbaCard)
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                .padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(10.dp)
                        .clip(AlbaShapes.chip)
                        .background(base)
                )
                Box(
                    modifier = Modifier
                        .width(136.dp)
                        .height(20.dp)
                        .clip(AlbaShapes.chip)
                        .background(base)
                )
            }
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(26.dp)
                    .clip(AlbaShapes.chip)
                    .background(base)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // tarjeta simulada con las 8 filas reales del comprobante
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AlbaShapes.tarjeta)
                .background(AlbaCard)
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            val anchosDerecha = intArrayOf(96, 128, 144, 92, 100, 152, 98, 134)
            for (indice in 0 until 8) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(82.dp)
                            .height(12.dp)
                            .clip(AlbaShapes.chip)
                            .background(base)
                    )
                    Box(
                        modifier = Modifier
                            .width(anchosDerecha[indice].dp)
                            .height(12.dp)
                            .clip(AlbaShapes.chip)
                            .background(base)
                    )
                }
                if (indice < 7) {
                    HorizontalDivider(color = AlbaBorder)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // tres botones simulados al pie (editar, mis solicitudes, inicio)
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(AlbaShapes.boton)
                    .background(base)
            )
            if (i < 2) Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ticket minimalista animado que traza el circulo, dibuja el check y emite un pulso suave
@Composable
fun CheckAnimadoExito(
    modifier: Modifier = Modifier,
    color: Color = AlbaOrange,
    tamano: Dp = 36.dp
) {
    val progresoCirculo = remember { Animatable(0f) }
    val progresoTicket = remember { Animatable(0f) }
    val progresoPulso = remember { Animatable(0f) }
    val escalaPop = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        progresoCirculo.animateTo(1f, tween(340, easing = EaseOutCubic))
        coroutineScope {
            launch {
                progresoTicket.animateTo(1f, tween(260, easing = EaseOutCubic))
            }
            launch {
                escalaPop.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.52f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                progresoPulso.animateTo(1f, tween(520, easing = EaseOutCubic))
            }
        }
    }

    Canvas(
        modifier = modifier
            .size(tamano)
            .graphicsLayer {
                scaleX = escalaPop.value
                scaleY = escalaPop.value
            }
    ) {
        val trazo = 2.4.dp.toPx()
        val centro = Offset(size.width / 2f, size.height / 2f)
        val radioBase = (size.minDimension / 2f) - trazo

        // onda expansiva minimalista que se desvanece alrededor del check
        if (progresoPulso.value > 0f && progresoPulso.value < 1f) {
            val alphaOnda = (1f - progresoPulso.value) * 0.45f
            drawCircle(
                color = color.copy(alpha = alphaOnda),
                radius = radioBase * (1f + 0.45f * progresoPulso.value),
                center = centro,
                style = Stroke(width = 1.6.dp.toPx())
            )
        }

        // relleno tintado suave una vez completado el check
        drawCircle(
            color = color.copy(alpha = 0.14f * progresoTicket.value),
            radius = radioBase,
            center = centro
        )

        // arco circular que se dibuja progresivamente de 0 a 360 grados
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progresoCirculo.value,
            useCenter = false,
            topLeft = Offset(trazo, trazo),
            size = Size(size.width - trazo * 2f, size.height - trazo * 2f),
            style = Stroke(width = trazo, cap = StrokeCap.Round)
        )

        // trazo del ticket dibujado en dos segmentos fluidos
        val t = progresoTicket.value
        if (t > 0f) {
            val p1 = Offset(size.width * 0.28f, size.height * 0.52f)
            val p2 = Offset(size.width * 0.44f, size.height * 0.68f)
            val p3 = Offset(size.width * 0.73f, size.height * 0.36f)

            val pathCheck = Path().apply {
                moveTo(p1.x, p1.y)
                if (t < 0.45f) {
                    val f = t / 0.45f
                    lineTo(
                        p1.x + (p2.x - p1.x) * f,
                        p1.y + (p2.y - p1.y) * f
                    )
                } else {
                    lineTo(p2.x, p2.y)
                    val f = (t - 0.45f) / 0.55f
                    lineTo(
                        p2.x + (p3.x - p2.x) * f,
                        p2.y + (p3.y - p2.y) * f
                    )
                }
            }

            drawPath(
                path = pathCheck,
                color = color,
                style = Stroke(
                    width = trazo,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

// opcion con etiqueta e icono opcional para el selector ModalBottomSheet
@Immutable
data class OpcionSelector(
    val texto: String,
    val icono: ImageVector? = null
)

// selector moderno con flecha que rota 180 grados y ModalBottomSheet con check naranja
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorBottomSheetAlba(
    tituloSheet: String,
    valorActual: String,
    placeholder: String,
    opciones: List<OpcionSelector>,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier,
    esError: Boolean = false,
    fondoCampo: Color = AlbaCard
) {
    var abierto by remember { mutableStateOf(false) }
    val rotacionFlecha by animateFloatAsState(
        targetValue = if (abierto) 180f else 0f,
        animationSpec = tween(durationMillis = 260, easing = EaseOutCubic),
        label = "rotacionFlechaSelector"
    )
    val colorBorde by animateColorAsState(
        targetValue = when {
            esError -> AlbaError
            abierto -> AlbaOrange
            else -> AlbaBorder
        },
        animationSpec = tween(durationMillis = 220),
        label = "bordeSelector"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(AlbaShapes.campo)
            .background(fondoCampo)
            .border(1.dp, colorBorde, AlbaShapes.campo)
            .clickable { abierto = true }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = valorActual.ifBlank { placeholder },
            color = if (valorActual.isBlank()) AlbaTextSecondary else AlbaWhite,
            fontSize = 15.sp
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = if (abierto) AlbaOrange else AlbaTextSecondary,
            modifier = Modifier.graphicsLayer { rotationZ = rotacionFlecha }
        )
    }

    if (abierto) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { abierto = false },
            sheetState = sheetState,
            containerColor = AlbaCard,
            contentColor = AlbaWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = AlbaBorder) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = tituloSheet,
                    color = AlbaWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                opciones.forEach { opcion ->
                    val seleccionada = opcion.texto == valorActual
                    val fondoFila = if (seleccionada) AlbaOrange.copy(alpha = 0.14f) else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(AlbaShapes.boton)
                            .background(fondoFila)
                            .clickable {
                                alSeleccionar(opcion.texto)
                                abierto = false
                            }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            opcion.icono?.let { ico ->
                                Icon(
                                    imageVector = ico,
                                    contentDescription = null,
                                    tint = if (seleccionada) AlbaOrange else AlbaTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = opcion.texto,
                                color = if (seleccionada) AlbaOrange else AlbaWhite,
                                fontSize = 15.sp,
                                fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        if (seleccionada) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Seleccionado",
                                tint = AlbaOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ventana emergente minimalista con animacion suave de escala y desvanecimiento al abrir
@Composable
fun DialogoAnimadoAlba(
    alCerrar: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Dialog(
        onDismissRequest = alCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(240, easing = EaseOutCubic)) +
                scaleIn(initialScale = 0.93f, animationSpec = tween(260, easing = EaseOutCubic))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(AlbaShapes.dialogo)
                    .background(AlbaCard)
                    .border(1.dp, AlbaBorder, AlbaShapes.dialogo)
                    .padding(22.dp),
                content = contenido
            )
        }
    }
}
