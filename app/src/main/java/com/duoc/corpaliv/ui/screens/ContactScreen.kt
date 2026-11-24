package com.duoc.corpaliv.ui.screens

// pantalla del formulario para enviar una propuesta de ayuda o alianza
// usa selectores ModalBottomSheet modernos con iconos y dispara notificacion real al enviar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.ui.components.OpcionSelector
import com.duoc.corpaliv.ui.components.SelectorBottomSheetAlba
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

// opciones fijas con iconos fuera del composable para no recrearlas al escribir
private val OPCIONES_COLABORACION = listOf(
    OpcionSelector("Donación", Icons.Default.CardGiftcard),
    OpcionSelector("Alianza empresarial", Icons.Default.Business),
    OpcionSelector("Voluntariado", Icons.Default.Groups),
    OpcionSelector("Compra de productos", Icons.Default.ShoppingBag),
    OpcionSelector("Capacitación", Icons.Default.School)
)

private val OPCIONES_PROGRAMAS = listaProgramasAlbaLab.map { programa ->
    val icono = when (programa.id) {
        "taller_oficios" -> Icons.Default.Build
        "insercion_laboral" -> Icons.Default.Work
        else -> Icons.Default.AutoAwesome
    }
    OpcionSelector(programa.titulo, icono)
}

// formulario de contacto con selectores BottomSheet y notificacion heads-up al guardar
@Composable
fun ContactScreen(
    viewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alIrAResumen: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val modoColaboradorActivo by estadoViewModel.activo.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
        SelectorBottomSheetAlba(
            tituloSheet = "¿Cómo quieres ayudar?",
            valorActual = estado.colaboracion,
            placeholder = "Selecciona una opción",
            opciones = OPCIONES_COLABORACION,
            alSeleccionar = viewModel::onColaboracionChange,
            esError = estado.errores.colaboracion != null
        )
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
                Text(
                    text = "Acreditada para alianzas y donaciones bajo la Ley de Inclusión Laboral (Ley N° 21.015).",
                    color = AlbaTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EtiquetaCampo("PROGRAMA")
        SelectorBottomSheetAlba(
            tituloSheet = "Selecciona un programa",
            valorActual = estado.programa.ifBlank { OPCIONES_PROGRAMAS.first().texto },
            placeholder = OPCIONES_PROGRAMAS.first().texto,
            opciones = OPCIONES_PROGRAMAS,
            alSeleccionar = viewModel::onProgramaFormChange
        )

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
                    val appContext = context.applicationContext
                    val notificarAlFinal = modoColaboradorActivo == true
                    viewModel.guardarEnSqlite(
                        hayInternet = conectado,
                        onGuardado = { alIrAResumen() },
                        onComprobanteListo = { folio ->
                            if (notificarAlFinal) {
                                NotificacionHelper.enviarNotificacion(
                                    context = appContext,
                                    titulo = "Solicitud $folio registrada",
                                    mensaje = "Gracias por colaborar con Fundación CORPALIV. Revisaremos tu propuesta pronto."
                                )
                            }
                        }
                    )
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
