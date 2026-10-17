package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    alVolverAlInicio: () -> Unit
) {
    // lee los datos guardados en el viewmodel compartido
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmación Alba Lab") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen del Registro",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Nombre: ${estado.nombre}")
            Text(text = "Correo: ${estado.correo}")
            Text(text = "Dirección: ${estado.direccion}")
            Text(text = "Contraseña: ${"*".repeat(estado.clave.length)}")
            Text(text = "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = alVolverAlInicio,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al Inicio")
            }
        }
    }
}