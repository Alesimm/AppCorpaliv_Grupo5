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
import com.duoc.corpaliv.viewmodel.EstadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadoScreen(
    alVolver: () -> Unit,
    viewModel: EstadoViewModel = viewModel()
) {
    // observa el estado de activacion y del mensaje desde el viewmodel
    val estado = viewModel.activo.collectAsState()
    val mostrarMensaje = viewModel.mostrarMensaje.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferencias Alba Lab") }
            )
        }
    ) { innerPadding ->
        // si el estado aun es nulo muestra la pantalla de carga
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

            // anima el cambio de color entre verde activo y gris inactivo
            val colorAnimado by animateColorAsState(
                targetValue = if (estaActivo) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                animationSpec = tween(durationMillis = 500),
                label = ""
            )

            // calcula el texto del boton usando estado derivado
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

                // boton principal con color animado
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

                // mensaje de confirmacion con aparicion animada
                AnimatedVisibility(visible = mostrarMensaje.value) {
                    Text(
                        text = "Estado guardado exitosamente!",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = alVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Inicio")
                }
            }
        }
    }
}