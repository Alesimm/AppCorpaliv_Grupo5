package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.AvatarColaborador
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// pantalla 9 perfil del colaborador con contadores y gestion de solicitudes sqlite
@Composable
fun PerfilScreen(
    usuarioViewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alAbrirMenu: () -> Unit = {},
    alVerSolicitud: () -> Unit = {},
    alIrAFavoritos: () -> Unit = {},
    alNavegarBottom: (Screen) -> Unit = {}
) {
    val estado by usuarioViewModel.estado.collectAsState()
    val solicitudes by usuarioViewModel.listaSolicitudes.collectAsState()
    val favoritos by estadoViewModel.favoritos.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "PERFIL",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = {}
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = "",
                alNavegar = alNavegarBottom
            )
        },
        containerColor = AlbaBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            AvatarColaborador(tamano = 96)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = estado.nombreUsuarioActivo,
                color = AlbaWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = estado.correoUsuarioActivo,
                color = AlbaTextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // contadores en vivo de favoritos y solicitudes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                        .clickable { alIrAFavoritos() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${favoritos.size}",
                            color = AlbaWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "FAVORITOS",
                            color = AlbaTextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${solicitudes.size}",
                            color = AlbaWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SOLICITUDES",
                            color = AlbaTextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis solicitudes",
                    color = AlbaWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (solicitudes.any { it.estadoEnvio.startsWith("PENDIENTE") }) {
                    TextButton(onClick = { usuarioViewModel.sincronizarPendientes() }) {
                        Text(
                            text = "Sincronizar pendientes",
                            color = AlbaOrange,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (solicitudes.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                ) {
                    Text(
                        text = "No tienes solicitudes registradas. Ve a la pestaña Contacto para enviar una propuesta de colaboración.",
                        color = AlbaTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(solicitudes, key = { it.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(containerColor = AlbaCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                                .clickable {
                                    usuarioViewModel.verDetalleSolicitud(item)
                                    alVerSolicitud()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.folio,
                                        color = AlbaWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${item.colaboracion} · ${item.programa}",
                                        color = AlbaTextSecondary,
                                        fontSize = 13.sp
                                    )
                                    if (item.estadoEnvio.startsWith("PENDIENTE")) {
                                        Text(
                                            text = item.estadoEnvio,
                                            color = AlbaOrange,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { usuarioViewModel.eliminarSolicitud(item) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Eliminar solicitud",
                                        tint = AlbaTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Ver comprobante",
                                    tint = AlbaTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}