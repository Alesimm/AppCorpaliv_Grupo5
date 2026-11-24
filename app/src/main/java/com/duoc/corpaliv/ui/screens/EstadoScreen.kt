package com.duoc.corpaliv.ui.screens

// pantalla de configuracion con liquid toggles y notificaciones heads-up reales
// avisa claramente si el sistema android tiene bloqueado el permiso de notificaciones

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.components.BouncingDotsLoader
import com.duoc.corpaliv.ui.components.LiquidToggle
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalAlternarTema
import com.duoc.corpaliv.ui.utils.NotificacionHelper
import com.duoc.corpaliv.viewmodel.EstadoViewModel

// vista de configuracion para alertas nativas y aspecto visual
@Composable
fun EstadoScreen(
    alVolver: () -> Unit,
    viewModel: EstadoViewModel = viewModel()
) {
    val estado by viewModel.activo.collectAsStateWithLifecycle()
    val mostrarMensaje by viewModel.mostrarMensaje.collectAsStateWithLifecycle()
    val esOscuro by viewModel.esTemaOscuro.collectAsStateWithLifecycle()
    val alAlternarTema = LocalAlternarTema.current
    val context = LocalContext.current

    var permisoBloqueado by remember { mutableStateOf(false) }

    val pedirPermisoNotificaciones = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            permisoBloqueado = false
            viewModel.alternarEstado()
            NotificacionHelper.enviarNotificacion(
                context = context,
                titulo = "Modo Colaborador activo",
                mensaje = "Recibirás novedades de campañas y confirmación de tus solicitudes en CORPALIV."
            )
        } else {
            permisoBloqueado = true
        }
    }

    if (estado == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AlbaBlack),
            contentAlignment = Alignment.Center
        ) {
            BouncingDotsLoader(color = AlbaOrange)
        }
    } else {
        val estaActivo = estado == true

        val alternarModoColaborador: () -> Unit = {
            if (!estaActivo) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !NotificacionHelper.tienePermiso(context)
                ) {
                    pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else if (!NotificacionHelper.estaHabilitadoEnSistema(context)) {
                    permisoBloqueado = true
                } else {
                    permisoBloqueado = false
                    viewModel.alternarEstado()
                    NotificacionHelper.enviarNotificacion(
                        context = context,
                        titulo = "Modo Colaborador activo",
                        mensaje = "Recibirás novedades de campañas y confirmación de tus solicitudes en CORPALIV."
                    )
                }
            } else {
                permisoBloqueado = false
                viewModel.alternarEstado()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AlbaBlack)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configuración",
                color = AlbaWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            // tarjeta 1: notificaciones reales del modo colaborador
            Card(
                shape = AlbaShapes.tarjeta,
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AlbaShapes.tarjeta)
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                    .clickable { alternarModoColaborador() }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "NOTIFICACIONES Y ALERTAS",
                        color = AlbaOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (estaActivo) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsNone,
                                contentDescription = null,
                                tint = if (estaActivo) AlbaOrange else AlbaTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Modo Colaborador",
                                    color = AlbaWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (estaActivo) "Alertas activadas" else "Alertas desactivadas",
                                    color = AlbaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        LiquidToggle(
                            checked = estaActivo,
                            onCheckedChange = { alternarModoColaborador() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Recibe alertas locales de campañas de inclusión laboral y confirmación inmediata al enviar tus solicitudes.",
                        color = AlbaTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    AnimatedVisibility(visible = mostrarMensaje) {
                        Text(
                            text = "✓ Preferencia guardada",
                            color = AlbaGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    // aviso util solo si android tiene desactivado el permiso de notificaciones
                    AnimatedVisibility(visible = permisoBloqueado) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(AlbaShapes.boton)
                                .background(AlbaOrange.copy(alpha = 0.12f))
                                .border(1.dp, AlbaOrange.copy(alpha = 0.4f), AlbaShapes.boton)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Las notificaciones están bloqueadas en Android para esta app.",
                                color = AlbaOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Abrir ajustes del sistema →",
                                color = AlbaWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    NotificacionHelper.abrirAjustesNotificaciones(context)
                                }
                            )
                        }
                    }
                }
            }

            // tarjeta 2: aspecto visual conectado al efecto cortina
            Card(
                shape = AlbaShapes.tarjeta,
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AlbaShapes.tarjeta)
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                    .clickable { alAlternarTema() }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ASPECTO VISUAL",
                        color = AlbaPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (esOscuro) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                contentDescription = null,
                                tint = if (esOscuro) AlbaOrange else AlbaPurple,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (esOscuro) "Modo Oscuro" else "Modo Claro",
                                    color = AlbaWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (esOscuro) "Fondo de alto contraste" else "Colores institucionales claros",
                                    color = AlbaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        LiquidToggle(
                            checked = esOscuro,
                            onCheckedChange = { alAlternarTema() },
                            colorActivo = AlbaPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Alterna la apariencia de toda la aplicación conservando tu preferencia en el dispositivo.",
                        color = AlbaTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            BotonSecundario(
                texto = "VOLVER AL INICIO",
                alHacerClick = alVolver
            )
        }
    }
}
