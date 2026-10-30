package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.EtiquetaCategoriaPrograma
import com.duoc.corpaliv.ui.components.IlustracionPrograma
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
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
                rutaActual = if (soloFavoritos) Screen.Favoritos.route else Screen.Catalogo.route,
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
    val esOscuro = LocalEsTemaOscuro.current
    val colorCorazon = if (esFavorito) (if (esOscuro) AlbaWhite else Color(0xFF121212)) else AlbaTextSecondary

    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = AlbaCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            .clickable { alAbrirDetalle() }
    ) {
        Column {
            // cabecera con ilustracion vectorial de las sonrisas del logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(AlbaCard)
            ) {
                IlustracionPrograma(
                    idPrograma = programa.id,
                    modifier = Modifier.fillMaxSize(),
                    escala = if (programa.id == "autonomia_transicion") 0.85f else 0.92f
                )

                // etiqueta de categoria superior izquierda
                EtiquetaCategoriaPrograma(
                    idPrograma = programa.id,
                    categoria = programa.categoria,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                )

                // boton de corazon superior derecho conectado con datastore
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .clickable { alAlternarFavorito() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Guardar en favoritos",
                        tint = colorCorazon,
                        modifier = Modifier.size(20.dp)
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