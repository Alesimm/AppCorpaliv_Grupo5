package com.duoc.corpaliv.ui.screens

// cuenta del usuario activo accesible desde el menu lateral con contadores e historial
// permite ver comprobantes, sincronizar solicitudes pendientes y eliminar con confirmacion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.ui.components.AvatarColaborador
import com.duoc.corpaliv.ui.components.DialogoAnimadoAlba
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// vista de cuenta con informacion del colaborador e historial de solicitudes
@Composable
fun PerfilScreen(
    usuarioViewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alVerSolicitud: () -> Unit = {},
    alIrAFavoritos: () -> Unit = {}
) {
    val sesion by usuarioViewModel.sesionActiva.collectAsStateWithLifecycle()
    val solicitudes by usuarioViewModel.listaSolicitudes.collectAsStateWithLifecycle()
    val favoritos by estadoViewModel.favoritos.collectAsStateWithLifecycle()
    val estadoRecarga by usuarioViewModel.estadoRecarga.collectAsStateWithLifecycle()

    // al entrar a cuenta revisa automaticamente si el servidor ya esta activo para subir pendientes
    LaunchedEffect(Unit) {
        usuarioViewModel.recargarSolicitudesServidor(silencioso = true)
    }

    val hayPendientes = remember(solicitudes) {
        solicitudes.any { it.estadoEnvio.startsWith("PENDIENTE") }
    }

    val scope = rememberCoroutineScope()
    var solicitudAEliminar by remember { mutableStateOf<SolicitudEntity?>(null) }

    solicitudAEliminar?.let { solicitud ->
        DialogoAnimadoAlba(alCerrar = { solicitudAEliminar = null }) {
            Text(
                text = "Confirmar eliminación",
                color = AlbaWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "¿Estás seguro de que deseas eliminar la solicitud ${solicitud.folio}? Esta acción no se puede deshacer.",
                color = AlbaTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { solicitudAEliminar = null }) {
                    Text("CANCELAR", color = AlbaTextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        // cerramos primero el dialogo para evitar lag de ventana + relayout simultaneo
                        solicitudAEliminar = null
                        scope.launch {
                            delay(100)
                            usuarioViewModel.eliminarSolicitud(solicitud)
                        }
                    }
                ) {
                    Text("ELIMINAR", color = AlbaError, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        AvatarColaborador(tamano = 88)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = sesion.nombre,
            color = AlbaWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = sesion.correo,
            color = AlbaTextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = AlbaShapes.tarjeta,
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .weight(1f)
                    .clip(AlbaShapes.tarjeta)
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
                shape = AlbaShapes.tarjeta,
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hayPendientes) {
                    TextButton(
                        onClick = { usuarioViewModel.sincronizarPendientes() },
                        enabled = !estadoRecarga.cargando
                    ) {
                        Text(
                            text = "Sincronizar pendientes",
                            color = AlbaOrange,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(
                    onClick = { usuarioViewModel.recargarSolicitudesServidor() },
                    enabled = !estadoRecarga.cargando
                ) {
                    val anguloActual = if (estadoRecarga.cargando) {
                        val transicionGiro = rememberInfiniteTransition(label = "giro_solicitudes")
                        val angulo by transicionGiro.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 700, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "angulo_solicitudes"
                        )
                        angulo
                    } else {
                        0f
                    }
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar desde servidor",
                        tint = AlbaOrange,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(anguloActual)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = estadoRecarga.mensaje != null,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(260))
        ) {
            Text(
                text = estadoRecarga.mensaje.orEmpty(),
                color = if (estadoRecarga.esError) AlbaOrange else Color(0xFF4CAF50),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (solicitudes.isEmpty()) {
            Card(
                shape = AlbaShapes.tarjeta,
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(solicitudes, key = { it.id }) { item ->
                    Card(
                        shape = AlbaShapes.tarjeta,
                        colors = CardDefaults.cardColors(containerColor = AlbaCard),
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clip(AlbaShapes.tarjeta)
                            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
                                onClick = { solicitudAEliminar = item }
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
