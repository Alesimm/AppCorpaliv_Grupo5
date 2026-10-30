package com.duoc.corpaliv.ui.screens

// pantalla del formulario de colaboracion con validacion reactiva y persistencia en room
// se conecta con usuarioviewmodel, screen, apptopbar, appbottombar y nativeutils

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
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.hayConexionInternet
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// formulario de postulacion y vinculacion con captura de datos validados
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

            // campo de nombre con icono person y error reactivo
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

            // campo de correo con icono email y error reactivo
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

            // selector desplegable de modalidad de colaboracion
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

            // animacion funcional que despliega el campo rut unicamente al elegir alianza empresarial
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
                        shape = RoundedCornerShape(2.dp),
                        colors = coloresInputAlba(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // selector desplegable para elegir el programa social de interes
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

            // campo de texto para mensaje opcional con validacion visual de longitud maxima
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

            // valida el formulario, verifica red nativa, guarda en room y limpia los campos
            Button(
                onClick = {
                    if (viewModel.validarFormulario()) {
                        val conectado = hayConexionInternet(context)
                        viewModel.guardarEnSqlite(conectado) {
                            alIrAResumen()
                        }
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
        }
    }
}

// dibuja el rotulo superior de cada campo del formulario
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

// define la paleta oscura estandar para los campos de texto
@Composable
private fun coloresInputAlba() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = AlbaCard,
    unfocusedContainerColor = AlbaCard,
    focusedBorderColor = AlbaWhite,
    unfocusedBorderColor = AlbaBorder,
    focusedTextColor = AlbaWhite,
    unfocusedTextColor = AlbaWhite
)
