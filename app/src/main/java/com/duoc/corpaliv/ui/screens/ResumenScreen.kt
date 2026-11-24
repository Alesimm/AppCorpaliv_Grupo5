package com.duoc.corpaliv.ui.screens

// comprobante oficial con transicion skeleton breve al crear una solicitud nueva
// usa dialogo animado y selector ModalBottomSheet para editar la solicitud

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.components.CheckAnimadoExito
import com.duoc.corpaliv.ui.components.DialogoAnimadoAlba
import com.duoc.corpaliv.ui.components.OpcionSelector
import com.duoc.corpaliv.ui.components.SelectorBottomSheetAlba
import com.duoc.corpaliv.ui.components.SkeletonComprobantePulse
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.validation.validarMensaje
import com.duoc.corpaliv.validation.validarRut
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

private val OPCIONES_EDITAR_COLAB = listOf(
    OpcionSelector("Donación", Icons.Default.CardGiftcard),
    OpcionSelector("Alianza empresarial", Icons.Default.Business),
    OpcionSelector("Voluntariado", Icons.Default.Groups),
    OpcionSelector("Compra de productos", Icons.Default.ShoppingBag),
    OpcionSelector("Capacitación", Icons.Default.School)
)

// muestra skeleton breve al registrar una solicitud nueva y luego revela el comprobante con crossfade
@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    alIrAInicio: () -> Unit,
    alIrAPerfil: () -> Unit
) {
    val solicitud by viewModel.solicitudSeleccionada.collectAsStateWithLifecycle()
    val cargando by viewModel.cargandoComprobante.collectAsStateWithLifecycle()

    Crossfade(
        targetState = cargando,
        animationSpec = tween(durationMillis = 360),
        label = "transicionComprobante"
    ) { estaCargando ->
        if (estaCargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AlbaBlack)
                    .verticalScroll(rememberScrollState())
            ) {
                SkeletonComprobantePulse()
            }
        } else {
            val item = solicitud
            if (item == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AlbaBlack)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay comprobante activo",
                        color = AlbaWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Puedes enviar una solicitud desde la pestaña Contacto o revisar el historial en Cuenta.",
                        color = AlbaTextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BotonPrincipal(
                        texto = "IR A INICIO",
                        alHacerClick = alIrAInicio
                    )
                }
            } else {
                ContenidoComprobante(
                    solicitud = item,
                    alEditarSolicitud = { nuevaColab, nuevoRut, nuevoMsg ->
                        viewModel.actualizarSolicitudExistente(item, nuevaColab, nuevoRut, nuevoMsg)
                    },
                    alIrAInicio = alIrAInicio,
                    alIrAPerfil = alIrAPerfil
                )
            }
        }
    }
}

@Composable
private fun ContenidoComprobante(
    solicitud: SolicitudEntity,
    alEditarSolicitud: (String, String, String) -> Unit,
    alIrAInicio: () -> Unit,
    alIrAPerfil: () -> Unit
) {
    var mostrarDialogoEditar by remember { mutableStateOf(false) }

    if (mostrarDialogoEditar) {
        DialogoEditarSolicitud(
            solicitud = solicitud,
            alCerrar = { mostrarDialogoEditar = false },
            alGuardar = { nuevaColab, nuevoRut, nuevoMsg ->
                alEditarSolicitud(nuevaColab, nuevoRut, nuevoMsg)
                mostrarDialogoEditar = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            CheckAnimadoExito(tamano = 40.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Solicitud enviada",
                color = AlbaWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
        }

        Text(
            text = "El equipo de vinculación de CORPALIV revisará tu registro y te contactará.",
            color = AlbaTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            shape = AlbaShapes.tarjeta,
            colors = CardDefaults.cardColors(containerColor = AlbaCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlbaOrange, AlbaShapes.tarjeta)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FOLIO",
                        color = AlbaTextSecondary,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = solicitud.folio,
                        color = AlbaWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(AlbaShapes.chip)
                        .background(AlbaOrange.copy(alpha = 0.14f))
                        .border(1.dp, AlbaOrange, AlbaShapes.chip)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = solicitud.estadoEnvio,
                        color = AlbaOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = AlbaShapes.tarjeta,
            colors = CardDefaults.cardColors(containerColor = AlbaCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                FilaComprobante(titulo = "Fecha", valor = solicitud.fecha)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Colaboración", valor = solicitud.colaboracion)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Programa", valor = solicitud.programa)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Nombre", valor = solicitud.nombre)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Apellido", valor = solicitud.apellido.ifBlank { "-" })
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Correo", valor = solicitud.correo)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "RUT comercial", valor = solicitud.rutComercial)
                HorizontalDivider(color = AlbaBorder)
                FilaComprobante(titulo = "Mensaje", valor = solicitud.mensaje)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BotonSecundario(
            texto = "EDITAR SOLICITUD",
            alHacerClick = { mostrarDialogoEditar = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "MIS SOLICITUDES",
            alHacerClick = alIrAPerfil
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonPrincipal(
            texto = "INICIO",
            alHacerClick = alIrAInicio
        )
    }
}

@Composable
private fun FilaComprobante(titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = AlbaTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            color = AlbaWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DialogoEditarSolicitud(
    solicitud: SolicitudEntity,
    alCerrar: () -> Unit,
    alGuardar: (String, String, String) -> Unit
) {
    var colaboracion by remember { mutableStateOf(solicitud.colaboracion) }
    var rutComercial by remember {
        mutableStateOf(if (solicitud.rutComercial == "No aplica") "" else solicitud.rutComercial)
    }
    var mensaje by remember {
        mutableStateOf(if (solicitud.mensaje == "Sin mensaje adicional.") "" else solicitud.mensaje)
    }
    var errorRut by remember { mutableStateOf<String?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    DialogoAnimadoAlba(alCerrar = alCerrar) {
        Text(
            text = "Editar solicitud",
            color = AlbaWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Folio ${solicitud.folio}",
            color = AlbaOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectorBottomSheetAlba(
            tituloSheet = "Tipo de colaboración",
            valorActual = colaboracion,
            placeholder = "Selecciona una opción",
            opciones = OPCIONES_EDITAR_COLAB,
            alSeleccionar = { opcion ->
                colaboracion = opcion
                if (opcion != "Alianza empresarial") errorRut = null
            },
            fondoCampo = AlbaBlack
        )

        if (colaboracion == "Alianza empresarial") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = rutComercial,
                onValueChange = {
                    rutComercial = it
                    errorRut = null
                },
                label = { Text("RUT comercial") },
                placeholder = { Text("12345678-5", color = AlbaTextSecondary) },
                isError = errorRut != null,
                supportingText = {
                    errorRut?.let { Text(text = it, color = AlbaError) }
                },
                singleLine = true,
                shape = AlbaShapes.campo,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AlbaBlack,
                    unfocusedContainerColor = AlbaBlack,
                    focusedBorderColor = AlbaOrange,
                    unfocusedBorderColor = AlbaBorder,
                    focusedTextColor = AlbaWhite,
                    unfocusedTextColor = AlbaWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = {
                mensaje = it
                errorMensaje = null
            },
            label = { Text("Mensaje") },
            minLines = 3,
            isError = errorMensaje != null,
            supportingText = {
                errorMensaje?.let { Text(text = it, color = AlbaError) }
            },
            shape = AlbaShapes.campo,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AlbaBlack,
                unfocusedContainerColor = AlbaBlack,
                focusedBorderColor = AlbaOrange,
                unfocusedBorderColor = AlbaBorder,
                focusedTextColor = AlbaWhite,
                unfocusedTextColor = AlbaWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = alCerrar) {
                Text("CANCELAR", color = AlbaTextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = {
                    val errR = if (colaboracion == "Alianza empresarial") validarRut(rutComercial) else null
                    val errM = validarMensaje(mensaje)
                    errorRut = errR
                    errorMensaje = errM
                    if (errR == null && errM == null) {
                        alGuardar(colaboracion, rutComercial, mensaje)
                    }
                }
            ) {
                Text("GUARDAR", color = AlbaOrange, fontWeight = FontWeight.Bold)
            }
        }
    }
}
