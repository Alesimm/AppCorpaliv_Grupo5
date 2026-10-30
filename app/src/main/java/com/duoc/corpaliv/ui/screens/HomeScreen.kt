package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import com.duoc.corpaliv.ui.utils.obtenerWindowSizeClass
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// pantalla 2 inicio con saludo, programas destacados, opciones de colaboracion y windowsizeclass
@Composable
fun HomeScreen(
    usuarioViewModel: UsuarioViewModel,
    alAbrirMenu: () -> Unit,
    alIrAPerfil: () -> Unit,
    alSeleccionarPrograma: (ProgramaSocial) -> Unit,
    alSeleccionarColaboracion: (String) -> Unit,
    alNavegarBottom: (Screen) -> Unit
) {
    val estado by usuarioViewModel.estado.collectAsState()
    val windowSizeClass = obtenerWindowSizeClass()

    // adapta los margenes laterales segun si es celular compacto o tablet expandida
    val margenLateral = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 96.dp
        else -> 20.dp
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = Screen.Home.route,
                alNavegar = alNavegarBottom
            )
        },
        containerColor = AlbaBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = margenLateral, vertical = 20.dp)
        ) {
            Text(
                text = "BIENVENIDO",
                color = AlbaTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hola, ${estado.nombreUsuarioActivo}",
                color = AlbaWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Programas Destacados",
                color = AlbaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // carrusel horizontal de programas destacados igual al mockup
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaProgramasAlbaLab) { programa ->
                    TarjetaProgramaDestacado(
                        programa = programa,
                        alHacerClick = { alSeleccionarPrograma(programa) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Cómo puedes colaborar",
                color = AlbaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // grilla de 5 formas de colaborar identica a la lamina 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonOpcionColaborar(
                    titulo = "Donación",
                    icono = Icons.Default.CardGiftcard,
                    modifier = Modifier.weight(1f),
                    alHacerClick = { alSeleccionarColaboracion("Donación") }
                )
                BotonOpcionColaborar(
                    titulo = "Alianza\nempresarial",
                    icono = Icons.Default.Business,
                    modifier = Modifier.weight(1f),
                    alHacerClick = { alSeleccionarColaboracion("Alianza empresarial") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonOpcionColaborar(
                    titulo = "Voluntariado",
                    icono = Icons.Default.Groups,
                    modifier = Modifier.weight(1f),
                    alHacerClick = { alSeleccionarColaboracion("Voluntariado") }
                )
                BotonOpcionColaborar(
                    titulo = "Compra de\nproductos",
                    icono = Icons.Default.ShoppingBag,
                    modifier = Modifier.weight(1f),
                    alHacerClick = { alSeleccionarColaboracion("Compra de productos") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BotonOpcionColaborar(
                titulo = "Capacitación",
                icono = Icons.Default.School,
                modifier = Modifier.fillMaxWidth(),
                alHacerClick = { alSeleccionarColaboracion("Capacitación") }
            )
        }
    }
}

@Composable
private fun TarjetaProgramaDestacado(
    programa: ProgramaSocial,
    alHacerClick: () -> Unit
) {
    val esOscuro = LocalEsTemaOscuro.current
    val esCapacitacion = programa.id == "taller_oficios"
    val colorSuperior = if (esOscuro) (if (esCapacitacion) Color(0xFF2B2118) else Color(0xFF1F2B38)) else AlbaCard

    Box(
        modifier = Modifier
            .width(230.dp)
            .height(210.dp)
            .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            .background(
                Brush.verticalGradient(listOf(colorSuperior, AlbaCard)),
                RoundedCornerShape(2.dp)
            )
            .clickable { alHacerClick() }
            .padding(16.dp)
    ) {
        if (esCapacitacion) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color(0xFFD69A52), CircleShape)
                    .border(5.dp, Color(0xFF9C692E), CircleShape)
            )
        } else {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color(0xFFC29B78), CircleShape)
                    )
                }
            }
        }

        // etiqueta naranja superior
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(AlbaOrange, RoundedCornerShape(2.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = programa.categoria,
                color = AlbaBlack,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = programa.titulo,
            color = AlbaWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun BotonOpcionColaborar(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    alHacerClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(68.dp)
            .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            .background(AlbaCard, RoundedCornerShape(2.dp))
            .clickable { alHacerClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = titulo,
            tint = AlbaOrange,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = titulo,
            color = AlbaWhite,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}