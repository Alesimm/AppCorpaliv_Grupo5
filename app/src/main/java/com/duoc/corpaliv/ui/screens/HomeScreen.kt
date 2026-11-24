package com.duoc.corpaliv.ui.screens

// pantalla principal con saludo al usuario, carrusel paginado con pill dots y accesos rapidos
// usa la sesion desacoplada y el tamano de pantalla global para evitar recomposiciones extra

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.ui.components.EtiquetaCategoriaPrograma
import com.duoc.corpaliv.ui.components.IlustracionPrograma
import com.duoc.corpaliv.ui.components.PillDotsIndicator
import com.duoc.corpaliv.ui.components.colorDelPrograma
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.LocalWindowWidthSizeClass
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// vista principal responsiva con carrusel HorizontalPager y Pill Dots en tiempo real
@Composable
fun HomeScreen(
    usuarioViewModel: UsuarioViewModel,
    alSeleccionarPrograma: (ProgramaSocial) -> Unit,
    alSeleccionarColaboracion: (String) -> Unit
) {
    val sesion by usuarioViewModel.sesionActiva.collectAsStateWithLifecycle()
    val widthSizeClass = LocalWindowWidthSizeClass.current

    val margenLateral = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 96.dp
        else -> 20.dp
    }

    val pagerState = rememberPagerState(pageCount = { listaProgramasAlbaLab.size })
    val programaVisible = listaProgramasAlbaLab[pagerState.currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = margenLateral)) {
            Text(
                text = "BIENVENIDO",
                color = AlbaTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hola, ${sesion.nombre}",
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // carrusel deslizable con vista previa de la tarjeta siguiente
        HorizontalPager(
            state = pagerState,
            key = { listaProgramasAlbaLab[it].id },
            contentPadding = PaddingValues(start = margenLateral, end = margenLateral + 56.dp),
            pageSpacing = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) { indice ->
            val programa = listaProgramasAlbaLab[indice]
            TarjetaProgramaDestacado(
                programa = programa,
                alHacerClick = { alSeleccionarPrograma(programa) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // puntos indicadores que se estiran en pildora al deslizar con el dedo
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PillDotsIndicator(
                cantidad = listaProgramasAlbaLab.size,
                paginaActual = pagerState.currentPage,
                fraccionDesplazamiento = pagerState.currentPageOffsetFraction,
                colorActivo = colorDelPrograma(programaVisible.id)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Column(modifier = Modifier.padding(horizontal = margenLateral)) {
            Text(
                text = "Cómo puedes colaborar",
                color = AlbaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

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

// tarjeta destacada del carrusel con esquinas suaves e ilustracion en cache
@Composable
private fun TarjetaProgramaDestacado(
    programa: ProgramaSocial,
    alHacerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(AlbaShapes.tarjeta)
            .background(AlbaCard)
            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
            .clickable { alHacerClick() }
    ) {
        IlustracionPrograma(
            idPrograma = programa.id,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            escala = if (programa.id == "autonomia_transicion") 0.74f else 0.80f,
            desplazamientoX = 18.dp,
            desplazamientoY = 8.dp
        )

        EtiquetaCategoriaPrograma(
            idPrograma = programa.id,
            categoria = programa.categoria,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )

        Text(
            text = programa.titulo,
            color = AlbaWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

// acceso rapido de colaboracion con borde redondeado unificado
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
            .clip(AlbaShapes.boton)
            .background(AlbaCard)
            .border(1.dp, AlbaBorder, AlbaShapes.boton)
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
