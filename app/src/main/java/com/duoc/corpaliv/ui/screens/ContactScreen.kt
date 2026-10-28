package com.duoc.corpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.TarjetaContactoDesplegable
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.hayConexionInternet
import com.duoc.corpaliv.ui.utils.llamarPorTelefono
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// Pantalla del formulario de vinculación con validación reactiva, campo condicional y guardado en Room.
// Se conecta con UsuarioViewModel, NativeUtils (estado de red y llamadas) y ResumenScreen.
@Composable
fun ContactScreen(
    viewModel: UsuarioViewModel,
    alIrAResumen: () -> Unit = {},
    alAbrirMenu: () -> Unit = {},
    alIrAPerfil: () -> Unit = {},
    alNavegarBottom: (Screen) -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    val opcionesColaboracion = listOf(
        "Donación",
        "Alianza empresarial",
        "Voluntariado",
        "Compra de productos",
        "Capacitación"
    )
    val opcionesProgramas = listaProgramasAlbaLab.map { it.titulo }

    var menuAyudaAbierto by remember { mutableStateOf(false) }
    var menuProgramaAbierto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "CONTACTO",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = Screen.Contacto.route,
                alNavegar = alNavegarBottom
            )
        },
        containerColor = AlbaBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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

            // Campo de nombre con ícono de persona y mensaje de error reactivo debajo.
            EtiquetaCampo("NOMBRE")
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                placeholder = { Text("Nombre y apellido", color = AlbaTextSecondary) },
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
                shape = RoundedCornerShape(2.dp),
                colors = coloresInputAlba(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de correo con ícono de sobre y validación visual por campo.
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
                shape = RoundedCornerShape(2.dp),
                colors = coloresInputAlba(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector desplegable para elegir la modalidad de colaboración.
            EtiquetaCampo("¿CÓMO QUIERES AYUDAR?")
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(
                            1.dp,
                            if (estado.errores.colaboracion != null) AlbaError else AlbaBorder,
                            RoundedCornerShape(2.dp)
                        )
                        .background(AlbaCard, RoundedCornerShape(2.dp))
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
                    opcionesColaboracion.forEach { opcion ->
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

            // Animación funcional que despliega el campo RUT solo si selecciona Alianza empresarial.
            AnimatedVisibility(visible = estado.colaboracion == "Alianza empresarial") {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    EtiquetaCampo("RUT COMERCIAL")
                    OutlinedTextField(
                        value = estado.rutComercial,
                        onValueChange = viewModel::onRutComercialChange,
                        placeholder = { Text("12.345.678-5", color = AlbaTextSecondary) },
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
                        shape = RoundedCornerShape(2.dp),
                        colors = coloresInputAlba(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector desplegable de programa social.
            EtiquetaCampo("PROGRAMA")
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
                        .background(AlbaCard, RoundedCornerShape(2.dp))
                        .clickable { menuProgramaAbierto = true }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = estado.programa.ifBlank { opcionesProgramas.first() },
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
                    opcionesProgramas.forEach { prog ->
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

            // campo de texto para el mensaje opcional con validacion visual de longitud maxima
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
                shape = RoundedCornerShape(2.dp),
                colors = coloresInputAlba(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Valida los datos, consulta el recurso nativo de red y guarda la solicitud en Room.
            Button(
                onClick = {
                    if (viewModel.validarFormulario()) {
                        val conectado = hayConexionInternet(context)
                        viewModel.guardarEnSqlite(conectado)
                        alIrAResumen()
                    }
                },
                shape = RoundedCornerShape(2.dp),
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

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "DIRECTORIO DEMOSTRATIVO CORPALIV",
                color = AlbaOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Tarjetas desplegables con datos ficticios conectadas al marcador telefónico nativo.
            TarjetaContactoDesplegable(
                area = "Coordinación Alba Lab y Empresas",
                encargado = "Equipo de Vinculación (Dato ficticio)",
                telefono = "+56900000000",
                correo = "contacto@ejemplo.cl",
                alLlamar = { num -> llamarPorTelefono(context, num) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TarjetaContactoDesplegable(
                area = "Área de Voluntariado y Donaciones",
                encargado = "Coordinación Social (Dato ficticio)",
                telefono = "+56200000000",
                correo = "alianzas@ejemplo.cl",
                alLlamar = { num -> llamarPorTelefono(context, num) }
            )
        }
    }
}

// Dibuja la etiqueta superior de cada campo del formulario.
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

// Configura los colores oscuros estándar para los campos de texto del formulario.
@Composable
private fun coloresInputAlba() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = AlbaCard,
    unfocusedContainerColor = AlbaCard,
    focusedBorderColor = AlbaWhite,
    unfocusedBorderColor = AlbaBorder,
    focusedTextColor = AlbaWhite,
    unfocusedTextColor = AlbaWhite
)