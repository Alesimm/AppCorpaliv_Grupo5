package com.duoc.corpaliv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro

// componentes graficos vectoriales de los programas sociales basados en el logo institucional
// se conecta con HomeScreen, CatalogScreen y DetalleScreen respetando modo claro y modo oscuro

// retorna el color institucional caracteristico asociado a cada programa social
fun colorDelPrograma(id: String): Color = when (id) {
    "taller_oficios" -> Color(0xFFFFC21A)
    "insercion_laboral" -> Color(0xFF2FCB7E)
    "autonomia_transicion" -> Color(0xFF9A8DF5)
    else -> AlbaOrange
}

// etiqueta de categoria estilizada con contraste adaptativo para modo claro y modo oscuro
@Composable
fun EtiquetaCategoriaPrograma(
    idPrograma: String,
    categoria: String,
    modifier: Modifier = Modifier
) {
    val esOscuro = LocalEsTemaOscuro.current

    val (colorFondo, colorBorde, colorTexto) = if (esOscuro) {
        when (idPrograma) {
            "taller_oficios" -> Triple(Color(0xFF1E1700), Color(0xFFFFC21A).copy(alpha = 0.7f), Color(0xFFFFC21A))
            "insercion_laboral" -> Triple(Color(0xFF0D251A), Color(0xFF2FCB7E).copy(alpha = 0.7f), Color(0xFF2FCB7E))
            "autonomia_transicion" -> Triple(Color(0xFF18152A), Color(0xFF9A8DF5).copy(alpha = 0.7f), Color(0xFF9A8DF5))
            else -> Triple(Color(0xFF261505), AlbaOrange.copy(alpha = 0.7f), AlbaOrange)
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
            .background(colorFondo, RoundedCornerShape(2.dp))
            .border(1.dp, colorBorde, RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
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

// dibuja las sonrisas y arcos vectoriales oficiales del logo sin colisionar con las etiquetas
@Composable
fun IlustracionPrograma(
    idPrograma: String,
    modifier: Modifier = Modifier,
    escala: Float = 1.0f,
    desplazamientoX: Dp = 0.dp,
    desplazamientoY: Dp = 0.dp
) {
    Canvas(modifier = modifier) {
        val ancho = size.width
        val alto = size.height
        val trazoPx = 14.dp.toPx() * escala

        when (idPrograma) {
            "taller_oficios" -> {
                // tres arcos en u concentricos hacia abajo con opacidades 100%, 60% y 30%
                val colorBase = Color(0xFFFFC21A)
                val radioMayor = 70.dp.toPx() * escala
                val cx = (ancho / 2f) + desplazamientoX.toPx()
                val cy = (alto * 0.38f) + desplazamientoY.toPx()

                val arcos = listOf(
                    radioMayor to 1.0f,
                    46.dp.toPx() * escala to 0.60f,
                    22.dp.toPx() * escala to 0.30f
                )

                arcos.forEach { (r, alpha) ->
                    drawArc(
                        color = colorBase.copy(alpha = alpha),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(width = trazoPx, cap = StrokeCap.Round)
                    )
                }
            }
            "insercion_laboral" -> {
                // onda continua de cinco semicirculos mas segunda onda inferior al 35%
                val colorBase = Color(0xFF2FCB7E)
                val rOnda = 14.dp.toPx() * escala
                val anchoOnda = 10f * rOnda
                val desplazamientoOndaY = 26.dp.toPx() * escala
                val startX = (ancho / 2f) - (anchoOnda / 2f) + desplazamientoX.toPx()
                val cy = (alto * 0.48f) + desplazamientoY.toPx()

                fun trazarOnda(offsetY: Float): Path {
                    val path = Path()
                    path.moveTo(startX, offsetY)
                    var xActual = startX
                    for (i in 0 until 5) {
                        val esArcoArriba = (i % 2 == 0)
                        val rect = Rect(
                            left = xActual,
                            top = offsetY - rOnda,
                            right = xActual + (2f * rOnda),
                            bottom = offsetY + rOnda
                        )
                        if (esArcoArriba) {
                            path.arcTo(rect, startAngleDegrees = 180f, sweepAngleDegrees = -180f, forceMoveTo = false)
                        } else {
                            path.arcTo(rect, startAngleDegrees = 180f, sweepAngleDegrees = 180f, forceMoveTo = false)
                        }
                        xActual += (2f * rOnda)
                    }
                    return path
                }

                // onda secundaria inferior al 35% de opacidad
                drawPath(
                    path = trazarOnda(cy + desplazamientoOndaY),
                    color = colorBase.copy(alpha = 0.35f),
                    style = Stroke(width = trazoPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // onda principal superior al 100% de opacidad
                drawPath(
                    path = trazarOnda(cy),
                    color = colorBase,
                    style = Stroke(width = trazoPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            "autonomia_transicion" -> {
                // tres arcos hacia arriba concentricos con opacidades 30%, 60% y 100% mas pastilla vertical
                val colorBase = Color(0xFF9A8DF5)
                val rMayor = 90.dp.toPx() * escala
                val cx = (ancho / 2f) + (8.dp.toPx() * escala) + desplazamientoX.toPx()
                val cy = (alto * 0.78f) + desplazamientoY.toPx()

                val arcos = listOf(
                    rMayor to 0.30f,
                    62.dp.toPx() * escala to 0.60f,
                    34.dp.toPx() * escala to 1.0f
                )

                arcos.forEach { (r, alpha) ->
                    drawArc(
                        color = colorBase.copy(alpha = alpha),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(width = trazoPx, cap = StrokeCap.Round)
                    )
                }

                // pastilla vertical estilizada al costado izquierdo simulando la letra i del logo
                val anchoPastilla = trazoPx
                val altoPastilla = 32.dp.toPx() * escala
                val pastillaX = cx - rMayor - (4.dp.toPx() * escala)
                val pastillaY = cy - rMayor + (12.dp.toPx() * escala)

                drawRoundRect(
                    color = colorBase,
                    topLeft = Offset(pastillaX, pastillaY),
                    size = Size(anchoPastilla, altoPastilla),
                    cornerRadius = CornerRadius(anchoPastilla / 2f, anchoPastilla / 2f)
                )
            }
            else -> {
                // sonrisa generica alternativa para otros identificadores de programas
                val r = 40.dp.toPx() * escala
                val cx = (ancho / 2f) + desplazamientoX.toPx()
                val cy = (alto / 2f) + desplazamientoY.toPx()
                drawArc(
                    color = AlbaOrange,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = trazoPx, cap = StrokeCap.Round)
                )
            }
        }
    }
}
