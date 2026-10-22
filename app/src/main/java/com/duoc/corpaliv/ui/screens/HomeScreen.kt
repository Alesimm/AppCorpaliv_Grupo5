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
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme
import com.duoc.corpaliv.ui.utils.obtenerWindowSizeClass

@Composable
fun HomeScreen(
    alIrACatalogo: () -> Unit = {},
    alIrAContacto: () -> Unit = {},
    alIrAEstado: () -> Unit = {}
) {
    // mide el ancho de la pantalla actual para adaptar los margenes
    val windowSizeClass = obtenerWindowSizeClass()

    // ajusta el margen lateral segun si es celular compacto o tablet expandida
    val margenLateral = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 100.dp
        else -> 24.dp
    }

    Scaffold(
        topBar = {
            // usa el componente topbar reutilizable
            AppTopBar(titulo = "Alba Lab - CORPALIV")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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

            // usa los botones reutilizados desde el paquete components
            BotonPrincipal(
                texto = "Ver Programas Sociales",
                alHacerClick = alIrACatalogo
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotonSecundario(
                texto = "Quiero Colaborar",
                alHacerClick = alIrAContacto
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotonSecundario(
                texto = "Modo Colaborador (Estado)",
                alHacerClick = alIrAEstado
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppCorpaliv_Grupo5Theme {
        HomeScreen()
    }
}