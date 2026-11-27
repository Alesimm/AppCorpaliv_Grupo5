package com.duoc.corpaliv.ui.screens

// catalogo de programas sociales y vista filtrada de favoritos
// incluye boton de corazon con animacion heart burst y filtrado en memoria con remember

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.ui.components.BotonFavoritoBurst
import com.duoc.corpaliv.ui.components.EtiquetaCategoriaPrograma
import com.duoc.corpaliv.ui.components.IlustracionPrograma
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.EstadoViewModel

// lista vertical de programas sociales con filtro opcional de favoritos
@Composable
fun CatalogScreen(
    estadoViewModel: EstadoViewModel,
    soloFavoritos: Boolean = false,
    alSeleccionarPrograma: (ProgramaSocial) -> Unit
) {
    val favoritos by estadoViewModel.favoritos.collectAsStateWithLifecycle()

    val programasMostrar = remember(favoritos, soloFavoritos) {
        if (soloFavoritos) {
            listaProgramasAlbaLab.filter { favoritos.contains(it.id) }
        } else {
            listaProgramasAlbaLab
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item(key = "titulo_catalogo") {
            Text(
                text = if (soloFavoritos) "Mis Favoritos" else "Programas",
                color = AlbaWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (programasMostrar.isEmpty()) {
            item(key = "sin_favoritos") {
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
            TarjetaProgramaCatalogo(
                programa = programa,
                esFavorito = esFavorito,
                alAlternarFavorito = { estadoViewModel.alternarFavorito(programa.id) },
                alAbrirDetalle = { alSeleccionarPrograma(programa) }
            )
        }
    }
}

// tarjeta redondeada del catalogo con ilustracion vectorial y heart burst en favoritos
@Composable
private fun TarjetaProgramaCatalogo(
    programa: ProgramaSocial,
    esFavorito: Boolean,
    alAlternarFavorito: () -> Unit,
    alAbrirDetalle: () -> Unit
) {
    Card(
        shape = AlbaShapes.tarjeta,
        colors = CardDefaults.cardColors(containerColor = AlbaCard),
        modifier = Modifier
            .fillMaxWidth()
            .clip(AlbaShapes.tarjeta)
            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
            .clickable { alAbrirDetalle() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp)
                    .background(AlbaCard)
            ) {
                IlustracionPrograma(
                    idPrograma = programa.id,
                    descripcion = programa.titulo,
                    conDegradadoInferior = true,
                    colorFundidoInferior = AlbaCard,
                    modifier = Modifier.fillMaxSize()
                )

                EtiquetaCategoriaPrograma(
                    idPrograma = programa.id,
                    categoria = programa.categoria,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AlbaCard.copy(alpha = 0.88f))
                        .border(1.dp, AlbaBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    BotonFavoritoBurst(
                        esFavorito = esFavorito,
                        alAlternar = alAlternarFavorito
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
