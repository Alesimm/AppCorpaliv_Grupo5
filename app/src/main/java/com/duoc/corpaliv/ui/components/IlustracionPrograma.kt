package com.duoc.corpaliv.ui.components

// portadas editoriales en webp liviano y etiquetas de categoria para cada programa
// mantiene los colores oficiales de cada programa y asegura contraste sobre fotografia

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.R
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import com.duoc.corpaliv.ui.theme.PaletaClara

// devuelve el color oficial de cada programa: amarillo, verde o morado
fun colorDelPrograma(id: String): Color = when (id) {
    "taller_oficios" -> Color(0xFFFFC21A)
    "insercion_laboral" -> Color(0xFF2FCB7E)
    "autonomia_transicion" -> Color(0xFF9A8DF5)
    else -> PaletaClara.acento
}

// devuelve la imagen webp liviana correspondiente a cada programa social
@DrawableRes
fun imagenDelPrograma(id: String): Int = when (id) {
    "taller_oficios" -> R.drawable.img_taller_oficios
    "insercion_laboral" -> R.drawable.img_insercion_laboral
    "autonomia_transicion" -> R.drawable.img_autonomia_transicion
    else -> R.drawable.img_taller_oficios
}

// pastilla redondeada con el nombre de la categoria en letras mayusculas
@Composable
fun EtiquetaCategoriaPrograma(
    idPrograma: String,
    categoria: String,
    modifier: Modifier = Modifier,
    sobreFoto: Boolean = true
) {
    val esOscuro = LocalEsTemaOscuro.current

    val (colorFondo, colorBorde, colorTexto) = if (sobreFoto) {
        // sobre fotografia usamos fondo oscuro translucido para garantizar lectura nitida
        when (idPrograma) {
            "taller_oficios" -> Triple(Color(0xCC1A1508), Color(0xFFFFC21A).copy(alpha = 0.75f), Color(0xFFFFD55A))
            "insercion_laboral" -> Triple(Color(0xCC0A1D14), Color(0xFF2FCB7E).copy(alpha = 0.75f), Color(0xFF5EE3A0))
            "autonomia_transicion" -> Triple(Color(0xCC151226), Color(0xFF9A8DF5).copy(alpha = 0.75f), Color(0xFFBCB2FF))
            else -> Triple(Color(0xCC1E1208), AlbaOrange.copy(alpha = 0.75f), AlbaOrange)
        }
    } else if (esOscuro) {
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

// muestra la fotografia editorial webp del programa adaptada para fundirse con modo oscuro
@Composable
fun IlustracionPrograma(
    idPrograma: String,
    modifier: Modifier = Modifier,
    descripcion: String = "Portada del programa",
    conDegradadoInferior: Boolean = false,
    colorFundidoInferior: Color = Color.Black.copy(alpha = 0.72f)
) {
    val esOscuro = LocalEsTemaOscuro.current

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = imagenDelPrograma(idPrograma)),
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // velo carbon sutil en modo oscuro para atenuar fotos claras y fusionarlas con el tema
        if (esOscuro) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF111113).copy(alpha = 0.24f))
            )
        }

        if (conDegradadoInferior) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = if (esOscuro) 0.20f else 0.14f),
                                0.38f to Color.Transparent,
                                1.0f to colorFundidoInferior
                            )
                        )
                    )
            )
        }
    }
}
