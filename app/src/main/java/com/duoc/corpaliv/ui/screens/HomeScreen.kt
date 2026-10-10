package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // da la estructura base con barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alba Lab - CORPALIV") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        // apila todo verticalmente
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp), // margenes para que no quede pegado a los bordes
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido a la red Alba Lab!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Conectamos tu intención de ayudar con oportunidades reales de inclusión social y laboral para personas con discapacidad múltiple.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // boton principal lleno
            Button(
                onClick = { /* TODO: navegar a catalogo */ },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Ver Programas Sociales")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // boton secundario solo con linea
            OutlinedButton(
                onClick = { /* TODO: navegar a formulario */ },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Quiero Colaborar")
            }
        }
    }
}

// permite ver el diseño directo en android studio
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppCorpaliv_Grupo5Theme {
        HomeScreen()
    }
}