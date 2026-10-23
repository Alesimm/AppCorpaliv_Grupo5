package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.compartirTextoNativo
import com.duoc.corpaliv.ui.utils.llamarPorTelefono
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// pantalla 5 detalle del programa con recursos nativos llamar y difundir
@Composable
fun DetalleScreen(
    usuarioViewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alVolver: () -> Unit = {},
    alQuieroColaborar: () -> Unit = {}
) {
    val programa by usuarioViewModel.programaSeleccionado.collectAsState()
    val favoritos by estadoViewModel.favoritos.collectAsState()
    val esFavorito = favoritos.contains(programa.id)
    val context = LocalContext.current

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
            // cabecera ilustrada con boton volver y corazon de favorito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2D2013), AlbaBlack)
                        )
                    )
                    .padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-24).dp, y = (-10).dp)
                        .background(Color(0xFFD6934A), CircleShape)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(AlbaBlack.copy(alpha = 0.7f))
                            .border(1.dp, AlbaBorder)
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
                            .size(42.dp)
                            .background(if (esFavorito) AlbaOrange else AlbaBlack.copy(alpha = 0.7f))
                            .border(1.dp, if (esFavorito) AlbaOrange else AlbaBorder)
                            .clickable { estadoViewModel.alternarFavorito(programa.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (esFavorito) AlbaBlack else AlbaWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // cuerpo con informacion, botones nativos llamar/difundir e impacto
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = programa.categoria.uppercase(),
                    color = AlbaOrange,
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

                // botones de recursos nativos llamar y difundir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { llamarPorTelefono(context, programa.telefonoContacto) },
                        shape = RoundedCornerShape(2.dp),
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
                        shape = RoundedCornerShape(2.dp),
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

                // tarjeta de impacto
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
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

                // tarjeta destino del aporte
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
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