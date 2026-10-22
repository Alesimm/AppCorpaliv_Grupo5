package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.utils.compartirTextoNativo

@Composable
fun CatalogScreen(alVolver: () -> Unit, alIrAContacto: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // usa la barra superior reutilizable con boton para volver atras
            AppTopBar(
                titulo = "Programas Alba Lab",
                alVolver = alVolver
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

            // usa los botones reutilizables del paquete components
            BotonPrincipal(
                texto = "Quiero Colaborar",
                alHacerClick = alIrAContacto
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ejecuta el recurso nativo de compartir los programas de la fundacion
            BotonSecundario(
                texto = "Compartir Programas",
                alHacerClick = {
                    compartirTextoNativo(
                        context,
                        "Conoce los programas de inclusión social y laboral de CORPALIV - Alba Lab."
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonSecundario(
                texto = "Volver al Inicio",
                alHacerClick = alVolver
            )
        }
    }
}