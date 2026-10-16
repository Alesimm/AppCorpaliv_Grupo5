package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(alVolver: () -> Unit, alIrAContacto: () -> Unit) {
    // estructura base de la pantalla de catalogo
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programas Alba Lab") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Catálogo de Programas Sociales",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "1. Taller Sociolaboral y Oficios")
            Text(text = "2. Programa Inclusión Laboral (Ley 21.015)")
            Text(text = "3. Capacitación y Concientización")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = alIrAContacto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quiero Colaborar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Inicio")
            }
        }
    }
}