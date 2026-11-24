package com.duoc.corpaliv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro

// dibuja las curvas y arcos de colores del logo oficial usando drawWithCache
// calcula las rutas una sola vez por tamano para mantener 60 a 120 fps al hacer scroll

// devuelve el color oficial de cada programa: amarillo, verde o morado
fun colorDelPrograma(id: String): Color = when (id) {
    "taller_oficios" -> Color(0xFFFFC21A)
    "insercion_laboral" -> Color(0xFF2FCB7E)
    "autonomia_transicion" -> Color(0xFF9A8DF5)
    else -> AlbaOrange
}

// pastilla redondeada con el nombre de la categoria en letras mayusculas
@Composable
fun EtiquetaCategoriaPrograma(
    idPrograma: String,
    categoria: String,
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current

    val (colorFondo, colorBorde, colorTexto) = if (esOscuro) {
        when (idPrograma) {
            "taller_oficios" -> Triple(Color(0xFF1E1700), Color(0xFFFFC21A).copy(alpha = 0.65f), Color(0xFFFFC21A))
            "insercion_laboral" -> Triple(Color(0xFF0D251A), Color(0xFF2FCB7E).copy(alpha = 0.65f), Color(0xFF2FCB7E))
            "autonomia_transicion" -> Triple(Color(0xFF18152A), Color(0xFF9A8DF5).copy(alpha = 0.65f), Color(0xFF9A8DF5))
            else -> Triple(Color(0xFF261505), AlbaOrange.copy(alpha = 0.65f), AlbaOrange)
        }
    } else {
        when (idPrograma) {
            "taller_oficios" -> Triple(Color(0xFFFFF6E5), Color(0xFFFFDDA0), Color(0xFFA36800))
            "insercion_laboral" -> Triple(Color(0xFFEAFBF2), Color(0xFFA4ECC7), Color(0xFF138A4F))
            "autonomia_transicion" -> Triple(Color(0xFFF1EFFF), Color(0xFFCDC6FC), Color(0xFF5E4FA7))
            else -> Triple(Color(0xFFFFF3EB), Color(0xFFFFCCAA), AlbaOrange)
        }
    }

    Box(
        modifier = modifier
            .background(colorFondo, AlbaShapes.chip)
            .border(1.dp, colorBorde, AlbaShapes.chip)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = categoria.uppercase(),
            color = colorTexto,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// dibuja en pantalla las curvas oficiales del logo con geometria en cache para cero lag
@Composable
fun IlustracionPrograma(
    idPrograma: String,
    modifier: Modifier = Modifier,
    escala: Float = 1.0f,
    desplazamientoX: Dp = 0.dp,
    desplazamientoY: Dp = 0.dp
) {
    Box(
        modifier = modifier.drawWithCache {
            val ancho = size.width
            val alto = size.height
            val trazoPx = 14.dp.toPx() * escala
            val estiloArco = Stroke(width = trazoPx, cap = StrokeCap.Round)
            val estiloOnda = Stroke(width = trazoPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val dx = desplazamientoX.toPx()
            val dy = desplazamientoY.toPx()

            when (idPrograma) {
                "taller_oficios" -> {
                    // tres arcos amarillos hacia abajo precalculados en cache
                    val colorBase = Color(0xFFFFC21A)
                    val cx = (ancho / 2f) + dx
                    val cy = (alto * 0.38f) + dy
                    val radios = floatArrayOf(70.dp.toPx() * escala, 46.dp.toPx() * escala, 22.dp.toPx() * escala)
                    val colores = arrayOf(colorBase, colorBase.copy(alpha = 0.60f), colorBase.copy(alpha = 0.30f))

                    onDrawBehind {
                        for (i in radios.indices) {
                            val r = radios[i]
                            drawArc(
                                color = colores[i],
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(cx - r, cy - r),
                                size = Size(r * 2f, r * 2f),
                                style = estiloArco
                            )
                        }
                    }
                }
                "insercion_laboral" -> {
                    // ola verde continua precalculada una sola vez por tamano
                    val colorBase = Color(0xFF2FCB7E)
                    val colorSecundario = colorBase.copy(alpha = 0.35f)
                    val rOnda = 14.dp.toPx() * escala
                    val anchoOnda = 10f * rOnda
                    val desplazamientoOndaY = 26.dp.toPx() * escala
                    val startX = (ancho / 2f) - (anchoOnda / 2f) + dx
                    val cy = (alto * 0.48f) + dy

                    fun crearOnda(offsetY: Float): Path {
                        val path = Path()
                        path.moveTo(startX, offsetY)
                        var xActual = startX
                        for (i in 0 until 5) {
                            val rect = Rect(
                                left = xActual,
                                top = offsetY - rOnda,
                                right = xActual + (2f * rOnda),
                                bottom = offsetY + rOnda
                            )
                            val sweep = if (i % 2 == 0) -180f else 180f
                            path.arcTo(rect, startAngleDegrees = 180f, sweepAngleDegrees = sweep, forceMoveTo = false)
                            xActual += (2f * rOnda)
                        }
                        return path
                    }

                    val pathSecundario = crearOnda(cy + desplazamientoOndaY)
                    val pathPrincipal = crearOnda(cy)

                    onDrawBehind {
                        drawPath(path = pathSecundario, color = colorSecundario, style = estiloOnda)
                        drawPath(path = pathPrincipal, color = colorBase, style = estiloOnda)
                    }
                }
                "autonomia_transicion" -> {
                    // tres arcos morados hacia arriba mas barra vertical precalculados
                    val colorBase = Color(0xFF9A8DF5)
                    val rMayor = 90.dp.toPx() * escala
                    val cx = (ancho / 2f) + (8.dp.toPx() * escala) + dx
                    val cy = (alto * 0.78f) + dy
                    val radios = floatArrayOf(rMayor, 62.dp.toPx() * escala, 34.dp.toPx() * escala)
                    val colores = arrayOf(colorBase.copy(alpha = 0.30f), colorBase.copy(alpha = 0.60f), colorBase)

                    val altoPastilla = 32.dp.toPx() * escala
                    val pastillaOffset = Offset(
                        cx - rMayor - (4.dp.toPx() * escala),
                        cy - rMayor + (12.dp.toPx() * escala)
                    )
                    val pastillaSize = Size(trazoPx, altoPastilla)
                    val pastillaRadio = CornerRadius(trazoPx / 2f, trazoPx / 2f)

                    onDrawBehind {
                        for (i in radios.indices) {
                            val r = radios[i]
                            drawArc(
                                color = colores[i],
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(cx - r, cy - r),
                                size = Size(r * 2f, r * 2f),
                                style = estiloArco
                            )
                        }
                        drawRoundRect(
                            color = colorBase,
                            topLeft = pastillaOffset,
                            size = pastillaSize,
                            cornerRadius = pastillaRadio
                        )
                    }
                }
                else -> {
                    val r = 40.dp.toPx() * escala
                    val cx = (ancho / 2f) + dx
                    val cy = (alto / 2f) + dy
                    val topLeft = Offset(cx - r, cy - r)
                    val arcSize = Size(r * 2f, r * 2f)

                    onDrawBehind {
                        drawArc(
                            color = AlbaOrange,
                            startAngle = 0f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = estiloArco
                        )
                    }
                }
            }
        }
    )
}
