package com.duoc.corpaliv.ui.screens

// vista completa de un programa social con botones nativos para llamar y compartir
// incorpora animacion heart burst en favorito y entrada suave de la ilustracion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.ui.components.BotonFavoritoBurst
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.IlustracionPrograma
import com.duoc.corpaliv.ui.components.colorDelPrograma
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import com.duoc.corpaliv.ui.utils.compartirTextoNativo
import com.duoc.corpaliv.ui.utils.llamarPorTelefono
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// detalle del programa con impacto social, testimonio y accion directa hacia contacto
@Composable
fun DetalleScreen(
    usuarioViewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alVolver: () -> Unit = {},
    alQuieroColaborar: () -> Unit = {}
) {
    val programa by usuarioViewModel.programaSeleccionado.collectAsStateWithLifecycle()
    val favoritos by estadoViewModel.favoritos.collectAsStateWithLifecycle()
    val esFavorito = favoritos.contains(programa.id)
    val esOscuro = LocalEsTemaOscuro.current
    val colorProg = colorDelPrograma(programa.id)
    val context = LocalContext.current

    // entrada sutil de zoom-out y aparicion para la cabecera del programa
    val escalaEntrada = remember { Animatable(1.05f) }
    val alphaEntrada = remember { Animatable(0f) }
    LaunchedEffect(programa.id) {
        escalaEntrada.snapTo(1.05f)
        alphaEntrada.snapTo(0f)
        alphaEntrada.animateTo(1f, tween(360, easing = EaseOutCubic))
        escalaEntrada.animateTo(1f, tween(420, easing = EaseOutCubic))
    }

    Scaffold(
        containerColor = AlbaBlack,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AlbaBlack)
                    .navigationBarsPadding()
            ) {
                HorizontalDivider(color = AlbaBorder)
                Box(modifier = Modifier.padding(16.dp)) {
                    BotonPrincipal(
                        texto = "QUIERO COLABORAR  →",
                        alHacerClick = {
                            usuarioViewModel.onProgramaFormChange(programa.titulo)
                            alQuieroColaborar()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(colorProg.copy(alpha = 0.16f))
            ) {
                IlustracionPrograma(
                    idPrograma = programa.id,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = escalaEntrada.value
                            scaleY = escalaEntrada.value
                            alpha = alphaEntrada.value
                        },
                    escala = if (programa.id == "autonomia_transicion") 1.05f else 1.15f,
                    desplazamientoY = 16.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(AlbaShapes.boton)
                            .background(AlbaCard)
                            .border(1.dp, AlbaBorder, AlbaShapes.boton)
                            .clickable { alVolver() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Volver",
                            tint = AlbaWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(AlbaShapes.boton)
                            .background(AlbaCard)
                            .border(1.dp, AlbaBorder, AlbaShapes.boton),
                        contentAlignment = Alignment.Center
                    ) {
                        BotonFavoritoBurst(
                            esFavorito = esFavorito,
                            alAlternar = { estadoViewModel.alternarFavorito(programa.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                val colorCategoria = if (esOscuro) {
                    colorProg
                } else {
                    when (programa.id) {
                        "taller_oficios" -> Color(0xFFA36800)
                        "insercion_laboral" -> Color(0xFF138A4F)
                        "autonomia_transicion" -> Color(0xFF5E4FA7)
                        else -> AlbaOrange
                    }
                }

                Text(
                    text = programa.categoria.uppercase(),
                    color = colorCategoria,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = programa.titulo,
                    color = AlbaWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = programa.descripcion,
                    color = AlbaTextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { llamarPorTelefono(context, programa.telefonoContacto) },
                        shape = AlbaShapes.boton,
                        border = BorderStroke(1.dp, AlbaBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AlbaCard,
                            contentColor = AlbaWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "LLAMAR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            compartirTextoNativo(
                                context,
                                "Conoce el programa ${programa.titulo} de Fundación CORPALIV (Alba Lab): ${programa.descripcion}"
                            )
                        },
                        shape = AlbaShapes.boton,
                        border = BorderStroke(1.dp, AlbaBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AlbaCard,
                            contentColor = AlbaWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "DIFUNDIR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = AlbaShapes.tarjeta,
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "IMPACTO",
                            color = AlbaOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = programa.impactoTitulo,
                            color = AlbaWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = programa.testimonio,
                            color = AlbaTextSecondary,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = programa.autorTestimonio,
                            color = AlbaTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    shape = AlbaShapes.tarjeta,
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "DESTINO DEL APORTE",
                            color = AlbaOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = programa.destinoAporte,
                            color = AlbaWhite,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
