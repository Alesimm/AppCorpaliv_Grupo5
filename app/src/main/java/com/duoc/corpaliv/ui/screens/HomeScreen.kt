package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme
import com.duoc.corpaliv.ui.utils.obtenerWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    alIrACatalogo: () -> Unit = {},
    alIrAContacto: () -> Unit = {},
    alIrAEstado: () -> Unit = {}
) {
    // obtiene el tamaño de la pantalla actual
    val windowSizeClass = obtenerWindowSizeClass()

    // define el margen dependiendo si es celular o tablet
    val margenLateral = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 100.dp
        else -> 24.dp
    }

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
                // aplica el margen dinamico segun la pantalla
                .padding(horizontal = margenLateral),
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

            // boton principal lleno conectado a la navegacion
            Button(
                onClick = alIrACatalogo,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Ver Programas Sociales")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // boton secundario solo con linea conectado a la navegacion
            OutlinedButton(
                onClick = alIrAContacto,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Quiero Colaborar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // boton para ir al panel de estado persistente y animaciones
            OutlinedButton(
                onClick = alIrAEstado,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Modo Colaborador (Estado)")
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