package com.duoc.corpaliv.ui.screens

// pantalla del formulario para enviar una propuesta de ayuda o alianza
// guarda la solicitud en sqlite y dispara notificacion local si el modo colaborador esta activo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.NotificacionHelper
import com.duoc.corpaliv.ui.utils.hayConexionInternet
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// listas fijas fuera del composable para no recrearlas al escribir en el formulario
private val OPCIONES_COLABORACION = listOf(
    "Donación",
    "Alianza empresarial",
    "Voluntariado",
    "Compra de productos",
    "Capacitación"
)
private val OPCIONES_PROGRAMAS = listaProgramasAlbaLab.map { it.titulo }

// formulario de contacto con validacion y confirmacion por notificacion local
@Composable
fun ContactScreen(
    viewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alIrAResumen: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val modoColaboradorActivo by estadoViewModel.activo.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var menuAyudaAbierto by remember { mutableStateOf(false) }
    var menuProgramaAbierto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Contacto",
            color = AlbaWhite,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Cuéntanos cómo quieres colaborar.",
            color = AlbaTextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        EtiquetaCampo("NOMBRE")
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            placeholder = { Text("Tu nombre", color = AlbaTextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AlbaTextSecondary
                )
            },
            singleLine = true,
            isError = estado.errores.nombre != null,
            supportingText = {
                estado.errores.nombre?.let { Text(text = it, color = AlbaError) }
            },
            shape = AlbaShapes.campo,
            colors = coloresInputAlba(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        EtiquetaCampo("APELLIDO")
        OutlinedTextField(
            value = estado.apellido,
            onValueChange = viewModel::onApellidoChange,
            placeholder = { Text("Tu apellido", color = AlbaTextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AlbaTextSecondary
                )
            },
            singleLine = true,
            isError = estado.errores.apellido != null,
            supportingText = {
                estado.errores.apellido?.let { Text(text = it, color = AlbaError) }
            },
            shape = AlbaShapes.campo,
            colors = coloresInputAlba(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        EtiquetaCampo("CORREO")
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            placeholder = { Text("nombre@ejemplo.cl", color = AlbaTextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = AlbaTextSecondary
                )
            },
            singleLine = true,
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let { Text(text = it, color = AlbaError) }
            },
            shape = AlbaShapes.campo,
            colors = coloresInputAlba(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        EtiquetaCampo("¿CÓMO QUIERES AYUDAR?")
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(AlbaShapes.campo)
                    .background(AlbaCard)
                    .border(
                        1.dp,
                        if (estado.errores.colaboracion != null) AlbaError else AlbaBorder,
                        AlbaShapes.campo
                    )
                    .clickable { menuAyudaAbierto = true }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = estado.colaboracion.ifBlank { "Selecciona una opción" },
                    color = if (estado.colaboracion.isBlank()) AlbaTextSecondary else AlbaWhite,
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AlbaTextSecondary
                )
            }

            DropdownMenu(
                expanded = menuAyudaAbierto,
                onDismissRequest = { menuAyudaAbierto = false },
                modifier = Modifier.background(AlbaCard)
            ) {
                OPCIONES_COLABORACION.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(text = opcion, color = AlbaWhite) },
                        onClick = {
                            viewModel.onColaboracionChange(opcion)
                            menuAyudaAbierto = false
                        }
                    )
                }
            }
        }
        estado.errores.colaboracion?.let {
            Text(
                text = it,
                color = AlbaError,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 12.dp)
            )
        }

        AnimatedVisibility(visible = estado.colaboracion == "Alianza empresarial") {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                EtiquetaCampo("RUT COMERCIAL")
                OutlinedTextField(
                    value = estado.rutComercial,
                    onValueChange = viewModel::onRutComercialChange,
                    placeholder = { Text("12345678-5", color = AlbaTextSecondary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = AlbaTextSecondary
                        )
                    },
                    singleLine = true,
                    isError = estado.errores.rutComercial != null,
                    supportingText = {
                        estado.errores.rutComercial?.let { Text(text = it, color = AlbaError) }
                    },
                    shape = AlbaShapes.campo,
                    colors = coloresInputAlba(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EtiquetaCampo("PROGRAMA")
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(AlbaShapes.campo)
                    .background(AlbaCard)
                    .border(1.dp, AlbaBorder, AlbaShapes.campo)
                    .clickable { menuProgramaAbierto = true }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = estado.programa.ifBlank { OPCIONES_PROGRAMAS.first() },
                    color = AlbaWhite,
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AlbaTextSecondary
                )
            }

            DropdownMenu(
                expanded = menuProgramaAbierto,
                onDismissRequest = { menuProgramaAbierto = false },
                modifier = Modifier.background(AlbaCard)
            ) {
                OPCIONES_PROGRAMAS.forEach { prog ->
                    DropdownMenuItem(
                        text = { Text(text = prog, color = AlbaWhite) },
                        onClick = {
                            viewModel.onProgramaFormChange(prog)
                            menuProgramaAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EtiquetaCampo("MENSAJE (OPCIONAL)")
        OutlinedTextField(
            value = estado.mensaje,
            onValueChange = viewModel::onMensajeChange,
            placeholder = { Text("Cuéntanos más detalles de tu propuesta...", color = AlbaTextSecondary) },
            minLines = 3,
            isError = estado.errores.mensaje != null,
            supportingText = {
                estado.errores.mensaje?.let { Text(text = it, color = AlbaError) }
            },
            shape = AlbaShapes.campo,
            colors = coloresInputAlba(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (viewModel.validarFormulario()) {
                    val conectado = hayConexionInternet(context)
                    viewModel.guardarEnSqlite(conectado) { folio ->
                        // si el usuario activo alertas en configuracion enviamos notificacion local real
                        if (modoColaboradorActivo == true) {
                            NotificacionHelper.enviarNotificacion(
                                context = context,
                                titulo = "Solicitud $folio registrada",
                                mensaje = "Gracias por colaborar con Fundación CORPALIV. Revisaremos tu propuesta pronto."
                            )
                        }
                        alIrAResumen()
                    }
                }
            },
            shape = AlbaShapes.boton,
            colors = ButtonDefaults.buttonColors(
                containerColor = AlbaWhite,
                contentColor = AlbaBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ENVIAR SOLICITUD",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        color = AlbaTextSecondary,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun coloresInputAlba() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = AlbaCard,
    unfocusedContainerColor = AlbaCard,
    focusedBorderColor = AlbaWhite,
    unfocusedBorderColor = AlbaBorder,
    focusedTextColor = AlbaWhite,
    unfocusedTextColor = AlbaWhite
)
