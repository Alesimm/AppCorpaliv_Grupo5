package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.components.TarjetaContactoDesplegable
import com.duoc.corpaliv.ui.utils.hayConexionInternet
import com.duoc.corpaliv.ui.utils.llamarPorTelefono
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

@Composable
fun ContactScreen(
    viewModel: UsuarioViewModel,
    alIrAResumen: () -> Unit,
    alVolver: () -> Unit
) {
    // escucha los cambios del formulario desde el viewmodel en tiempo real
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // llama al topbar reutilizable creado aparte
            AppTopBar(
                titulo = "Contacto y Vinculación",
                alVolver = alVolver
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Directorio de Contactos CORPALIV",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // lista de contactos desplegables de la empresa con llamada telefonica nativa
            TarjetaContactoDesplegable(
                area = "Inclusión Laboral (Ley 21.015)",
                encargado = "Coordinación Empresas Alba Lab",
                telefono = "+56222774985",
                correo = "inclusion@corpaliv.cl",
                alLlamar = { numero -> llamarPorTelefono(context, numero) }
            )

            TarjetaContactoDesplegable(
                area = "Talleres Sociolaborales y Voluntariado",
                encargado = "Dirección Social CORPALIV",
                telefono = "+56222774986",
                correo = "voluntariado@corpaliv.cl",
                alLlamar = { numero -> llamarPorTelefono(context, numero) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Formulario de Solicitud (Guardado SQLite)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // campo nombre con validacion visual
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre o Empresa") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // campo correo con validacion visual
            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo electrónico") },
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // campo clave ocultando caracteres y validando largo minimo
            OutlinedTextField(
                value = estado.clave,
                onValueChange = viewModel::onClaveChange,
                label = { Text("Contraseña de seguimiento") },
                visualTransformation = PasswordVisualTransformation(),
                isError = estado.errores.clave != null,
                supportingText = {
                    estado.errores.clave?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // campo direccion con validacion visual
            OutlinedTextField(
                value = estado.direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección o Comuna") },
                isError = estado.errores.direccion != null,
                supportingText = {
                    estado.errores.direccion?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // casilla para aceptar terminos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Acepto los términos y condiciones")
            }

            // valida el formulario, detecta internet nativo y guarda el registro en sqlite room
            BotonPrincipal(
                texto = "Registrar Solicitud",
                alHacerClick = {
                    if (viewModel.validarFormulario()) {
                        val conectado = hayConexionInternet(context)
                        viewModel.guardarEnSqlite(conectado)
                        alIrAResumen()
                    }
                }
            )

            BotonSecundario(
                texto = "Ver Historial de Solicitudes",
                alHacerClick = alIrAResumen
            )
        }
    }
}