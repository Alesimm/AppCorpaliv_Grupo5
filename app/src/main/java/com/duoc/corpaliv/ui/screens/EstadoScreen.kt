package com.duoc.corpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.EstadoViewModel

// pantalla de configuracion para activar alertas de ayuda y cambiar el tema
// guarda las preferencias directamente en el telefono usando datastore

// vista con botones grandes para prender alertas y alternar modo oscuro o claro
@Composable
fun EstadoScreen(
    alVolver: () -> Unit,
    alAbrirMenu: () -> Unit,
    alIrAPerfil: () -> Unit,
    alNavegarBottom: (Screen) -> Unit,
    viewModel: EstadoViewModel = viewModel()
) {
    val estado = viewModel.activo.collectAsState()
    val mostrarMensaje = viewModel.mostrarMensaje.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "CONFIGURACIÓN",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
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
        if (estado.value == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AlbaOrange)
            }
        } else {
            val estaActivo = estado.value!!

            val esOscuro by viewModel.esTemaOscuro.collectAsState()

            // color suave para el boton cuando las notificaciones estan apagadas
            val fondoInactivo = if (esOscuro) Color(0xFF262626) else Color(0xFFEBE7DD)

            // animacion de color: naranja cuando esta activo y gris cuando esta apagado
            val colorAnimado by animateColorAsState(
                targetValue = if (estaActivo) AlbaOrange else fondoInactivo,
                animationSpec = tween(durationMillis = 350),
                label = "colorBotonNotificaciones"
            )

            // texto del boton que cambia solo segun si esta activado o desactivado
            val textoBoton by remember(estaActivo) {
                derivedStateOf {
                    if (estaActivo) "DESACTIVAR NOTIFICACIONES" else "ACTIVAR NOTIFICACIONES"
                }
            }

            // color blanco para que el texto siempre se lea con claridad
            val colorTextoBoton = if (estaActivo) Color.White else AlbaWhite

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // tarjeta 1: boton para activar o desactivar las alertas de ayuda
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PREFERENCIAS DATASTORE",
                            color = AlbaOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Modo Colaborador Activo",
                            color = AlbaWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Guarda en tu dispositivo la preferencia para recibir alertas de campañas e inclusión laboral de CORPALIV.",
                            color = AlbaTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.alternarEstado() },
                            shape = RoundedCornerShape(2.dp),
                            border = if (!estaActivo) BorderStroke(1.dp, AlbaBorder) else null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorAnimado,
                                contentColor = colorTextoBoton
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = textoBoton,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AnimatedVisibility(visible = mostrarMensaje.value) {
                            Text(
                                text = "✓ Preferencia guardada en el dispositivo",
                                color = AlbaGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // tarjeta 2: boton para cambiar entre tema claro y tema oscuro
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = AlbaCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ASPECTO VISUAL",
                            color = AlbaPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (esOscuro) "Modo Oscuro Activo" else "Modo Claro Activo",
                            color = AlbaWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (esOscuro)
                                "Tema con fondo oscuro de alto contraste para reducir el consumo de batería."
                            else
                                "Tema claro con los colores corporativos oficiales de Fundación CORPALIV.",
                            color = AlbaTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.alternarTema() },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (esOscuro) AlbaOrange else AlbaPurple,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = if (esOscuro) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (esOscuro) "CAMBIAR A MODO CLARO" else "CAMBIAR A MODO OSCURO",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                BotonSecundario(
                    texto = "VOLVER AL INICIO",
                    alHacerClick = alVolver
                )
            }
        }
    }
}