package com.duoc.corpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.viewmodel.EstadoViewModel

@Composable
fun EstadoScreen(
    alVolver: () -> Unit,
    viewModel: EstadoViewModel = viewModel()
) {
    // observa el estado persistente de datastore y la visibilidad del aviso
    val estado = viewModel.activo.collectAsState()
    val mostrarMensaje = viewModel.mostrarMensaje.collectAsState()

    Scaffold(
        topBar = {
            // usa la barra superior reutilizable
            AppTopBar(
                titulo = "Preferencias Alba Lab",
                alVolver = alVolver
            )
        }
    ) { innerPadding ->
        // muestra el indicador circular mientras carga la preferencia desde datastore
        if (estado.value == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val estaActivo = estado.value!!

            // anima la transicion de color entre verde activo y gris inactivo
            val colorAnimado by animateColorAsState(
                targetValue = if (estaActivo) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                animationSpec = tween(durationMillis = 500),
                label = ""
            )

            // calcula el texto del boton usando estado derivado para optimizar recomposiciones
            val textoBoton by remember(estaActivo) {
                derivedStateOf { if (estaActivo) "Desactivar" else "Activar" }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Modo Colaborador Activo",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Recibe notificaciones de inclusión laboral y talleres de CORPALIV.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.alternarEstado() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        text = textoBoton,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // muestra el texto de confirmacion con una animacion fluida
                AnimatedVisibility(visible = mostrarMensaje.value) {
                    Text(
                        text = "Estado guardado exitosamente!",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                BotonSecundario(
                    texto = "Volver al Inicio",
                    alHacerClick = alVolver
                )
            }
        }
    }
}