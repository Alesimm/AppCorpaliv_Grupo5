package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.duoc.corpaliv.viewmodel.EstadoViewModel

// pantalla 4 catalogo de programas sociales y vista filtrada de mis favoritos
@Composable
fun CatalogScreen(
    estadoViewModel: EstadoViewModel,
    soloFavoritos: Boolean = false,
    alAbrirMenu: () -> Unit,
    alIrAPerfil: () -> Unit,
    alSeleccionarPrograma: (ProgramaSocial) -> Unit,
    alNavegarBottom: (Screen) -> Unit
) {
    val favoritos by estadoViewModel.favoritos.collectAsState()
    val programasMostrar = if (soloFavoritos) {
        listaProgramasAlbaLab.filter { favoritos.contains(it.id) }
    } else {
        listaProgramasAlbaLab
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (soloFavoritos) "FAVORITOS" else "CATÁLOGO",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = Screen.Catalogo.route,
                alNavegar = alNavegarBottom
            )
        },
        containerColor = AlbaBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                Text(
                    text = if (soloFavoritos) "Mis Favoritos" else "Programas",
                    color = AlbaWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
            }

            if (programasMostrar.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has marcado programas como favoritos. Toca el ícono del corazón en el catálogo para guardarlos.",
                        color = AlbaTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            items(programasMostrar, key = { it.id }) { programa ->
                val esFavorito = favoritos.contains(programa.id)
                TarjetaCatalogoMockup(
                    programa = programa,
                    esFavorito = esFavorito,
                    alAlternarFavorito = { estadoViewModel.alternarFavorito(programa.id) },
                    alAbrirDetalle = { alSeleccionarPrograma(programa) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaCatalogoMockup(
    programa: ProgramaSocial,
    esFavorito: Boolean,
    alAlternarFavorito: () -> Unit,
    alAbrirDetalle: () -> Unit
) {
    val esCapacitacion = programa.id == "taller_oficios"
    val colorIlustracion = if (esCapacitacion) Color(0xFF2B2118) else Color(0xFF1E2936)

    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = AlbaCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            .clickable { alAbrirDetalle() }
    ) {
        Column {
            // cabecera grafica con etiqueta de categoria y boton de corazon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(listOf(colorIlustracion, AlbaCard))
                    )
                    .padding(14.dp)
            ) {
                if (esCapacitacion) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = (-24).dp)
                            .background(Color(0xFFD69A52), CircleShape)
                            .border(5.dp, Color(0xFF9C692E), CircleShape)
                    )
                } else {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFC29B78), CircleShape)
                            )
                        }
                    }
                }

                // etiqueta superior izquierda
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(AlbaBlack.copy(alpha = 0.8f))
                        .border(1.dp, AlbaBorder)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = programa.categoria,
                        color = AlbaWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // boton de corazon superior derecho conectado a datastore
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.TopEnd)
                        .background(if (esFavorito) AlbaOrange else AlbaBlack.copy(alpha = 0.7f))
                        .border(1.dp, if (esFavorito) AlbaOrange else AlbaBorder)
                        .clickable { alAlternarFavorito() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Guardar en favoritos",
                        tint = if (esFavorito) AlbaBlack else AlbaWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = programa.titulo,
                    color = AlbaWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = programa.resumen,
                    color = AlbaTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}