package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.utils.compartirTextoNativo
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    alVolverAlInicio: () -> Unit
) {
    // observa tanto el formulario actual como la lista guardada en sqlite room
    val estado by viewModel.estado.collectAsState()
    val solicitudes by viewModel.listaSolicitudes.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Estados de Solicitud (SQLite)",
                alVolver = alVolverAlInicio
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // muestra la tarjeta con el ultimo registro ingresado en memoria compartida
            if (estado.nombre.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Resumen del Registro Actual",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "Nombre: ${estado.nombre}")
                        Text(text = "Correo: ${estado.correo}")
                        Text(text = "Dirección: ${estado.direccion}")
                        Text(text = "Contraseña: ${"*".repeat(estado.clave.length)}")
                        Text(text = "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial en SQLite (${solicitudes.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                // permite pasar todas las solicitudes pendientes a sincronizadas
                TextButton(onClick = { viewModel.sincronizarPendientes() }) {
                    Text("Sincronizar todo")
                }
            }

            // lista desplazable que muestra cada solicitud guardada en la tabla sqlite
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(solicitudes, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Folio: ${item.folio}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.estadoEnvio,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Colaborador: ${item.nombre}")
                            Text(text = "Correo: ${item.correo} | Dir: ${item.direccion}")

                            Spacer(modifier = Modifier.height(8.dp))

                            // botones para ejecutar update, compartir nativo y delete sobre cada fila
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.cambiarEstadoSolicitud(item) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cambiar Estado")
                                }
                                OutlinedButton(
                                    onClick = {
                                        compartirTextoNativo(
                                            context,
                                            "Solicitud CORPALIV ${item.folio} - Estado: ${item.estadoEnvio}"
                                        )
                                    }
                                ) {
                                    Text("Compartir")
                                }
                                TextButton(
                                    onClick = { viewModel.eliminarSolicitud(item) }
                                ) {
                                    Text("Borrar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            BotonPrincipal(
                texto = "Volver al Inicio",
                alHacerClick = alVolverAlInicio
            )
        }
    }
}