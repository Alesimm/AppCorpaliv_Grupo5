package com.duoc.corpaliv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// componente reutilizable para mostrar la misma barra superior en todas las pantallas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    alVolver: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = titulo) },
        navigationIcon = {
            // si la pantalla recibe la accion de volver, dibuja el boton atras
            if (alVolver != null) {
                TextButton(onClick = alVolver) {
                    Text(
                        text = "Atrás",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// boton principal reutilizable para evitar repetir estilos en cada vista
@Composable
fun BotonPrincipal(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorFondo: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = alHacerClick,
        colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = texto)
    }
}

// boton secundario reutilizable con borde para acciones de navegacion
@Composable
fun BotonSecundario(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = alHacerClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = texto)
    }
}

// tarjeta desplegable para mostrar cada area de contacto de la fundacion corpaliv
@Composable
fun TarjetaContactoDesplegable(
    area: String,
    encargado: String,
    telefono: String,
    correo: String,
    alLlamar: (String) -> Unit
) {
    // guarda si la tarjeta esta abierta o cerrada al tocarla
    var desplegado by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { desplegado = !desplegado },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = area,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (desplegado) "Ocultar ▲" else "Ver datos ▼",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // anima la apertura de los datos del contacto y el boton de llamada nativa
            AnimatedVisibility(visible = desplegado) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Encargado: $encargado", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Correo: $correo", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Teléfono: $telefono", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { alLlamar(telefono) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Llamar a $area")
                    }
                }
            }
        }
    }
}