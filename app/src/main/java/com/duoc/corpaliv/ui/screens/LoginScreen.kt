package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.R
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// pantalla inicial de login que muestra el logo institucional y valida el acceso del usuario
// se conecta con usuarioviewmodel para leer el estado reactivo y con appnavigation para ir al home
@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    alIngresar: () -> Unit = {}
) {
    // observa en tiempo real los datos del formulario y los mensajes de error desde el viewmodel
    val estado by viewModel.estado.collectAsState()

    // estados locales de interfaz para mostrar u ocultar la clave y abrir el dialogo de recuperacion
    var mostrarClave by remember { mutableStateOf(false) }
    var mostrarDialogoOlvido by remember { mutableStateOf(false) }
    var correoRecuperacion by remember { mutableStateOf("") }
    var errorCorreoRecuperacion by remember { mutableStateOf<String?>(null) }
    var mensajeRecuperacionEnviado by remember { mutableStateOf(false) }

    // expresion regular para validar que el correo de recuperacion tenga formato correcto
    val regexCorreo = remember { Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") }

    // dialogo demostrativo para simular la recuperacion de contrasena sin servidor externo
    if (mostrarDialogoOlvido) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoOlvido = false
                errorCorreoRecuperacion = null
                mensajeRecuperacionEnviado = false
            },
            containerColor = AlbaCard,
            titleContentColor = AlbaWhite,
            textContentColor = AlbaTextSecondary,
            title = {
                Text(
                    text = "Recuperar contraseña",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ingresa tu correo para simular la recuperación de tu clave.")
                    OutlinedTextField(
                        value = correoRecuperacion,
                        onValueChange = {
                            correoRecuperacion = it
                            errorCorreoRecuperacion = null
                            mensajeRecuperacionEnviado = false
                        },
                        placeholder = { Text("nombre@ejemplo.cl", color = AlbaTextSecondary) },
                        isError = errorCorreoRecuperacion != null,
                        supportingText = {
                            errorCorreoRecuperacion?.let { msg ->
                                Text(text = msg, color = AlbaError)
                            }
                        },
                        singleLine = true,
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
                    if (mensajeRecuperacionEnviado) {
                        Text(
                            text = "(Demo) Te enviaríamos un correo para recuperar tu clave",
                            color = AlbaGreen,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // valida el formato del correo antes de mostrar la confirmacion de la demo
                        if (correoRecuperacion.isBlank() || !regexCorreo.matches(correoRecuperacion.trim())) {
                            errorCorreoRecuperacion = "Ingresa un correo con formato válido"
                            mensajeRecuperacionEnviado = false
                        } else {
                            errorCorreoRecuperacion = null
                            mensajeRecuperacionEnviado = true
                        }
                    }
                ) {
                    Text("ENVIAR", color = AlbaOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoOlvido = false
                        errorCorreoRecuperacion = null
                        mensajeRecuperacionEnviado = false
                    }
                ) {
                    Text("CERRAR", color = AlbaTextSecondary)
                }
            }
        )
    }

    // contenedor principal con arrangement center para centrar verticalmente el logo y el formulario
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // cabecera centrada con el logo oficial mas grande sobre el fondo negro
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_corpaliv_oscuro),
                contentDescription = "Logo Fundación CORPALIV",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(285.dp)
                    .heightIn(min = 100.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Red sociolaboral Alba Lab. Ingresa para conocer los programas y colaborar.",
                color = AlbaTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(44.dp))

        // bloque del formulario con iconos, validaciones visuales por campo y datastore en recordarme
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "CORREO",
                color = AlbaTextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // campo de correo con icono leadingicon y mensaje de error reactivo en supportingtext
            OutlinedTextField(
                value = estado.correoLogin,
                onValueChange = viewModel::onCorreoLoginChange,
                placeholder = { Text("colaborador@ejemplo.cl", color = AlbaTextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = AlbaTextSecondary
                    )
                },
                isError = estado.errorLoginCorreo != null,
                supportingText = {
                    estado.errorLoginCorreo?.let {
                        Text(text = it, color = AlbaError)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AlbaCard,
                    unfocusedContainerColor = AlbaCard,
                    focusedBorderColor = AlbaOrange,
                    unfocusedBorderColor = AlbaBorder,
                    focusedTextColor = AlbaWhite,
                    unfocusedTextColor = AlbaWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CONTRASEÑA",
                color = AlbaTextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // campo de contrasena con icono de candado, boton de ojo para ocultar y validacion visual
            OutlinedTextField(
                value = estado.claveLogin,
                onValueChange = viewModel::onClaveLoginChange,
                placeholder = { Text("Mínimo 8 caracteres", color = AlbaTextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AlbaTextSecondary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { mostrarClave = !mostrarClave }) {
                        Icon(
                            imageVector = if (mostrarClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña",
                            tint = AlbaTextSecondary
                        )
                    }
                },
                visualTransformation = if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.errorLoginClave != null,
                supportingText = {
                    estado.errorLoginClave?.let {
                        Text(text = it, color = AlbaError)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AlbaCard,
                    unfocusedContainerColor = AlbaCard,
                    focusedBorderColor = AlbaOrange,
                    unfocusedBorderColor = AlbaBorder,
                    focusedTextColor = AlbaWhite,
                    unfocusedTextColor = AlbaWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // fila con casilla recordarme que guarda solo el correo en datastore y enlace de olvido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        viewModel.onRecordarCorreoChange(!estado.recordarCorreo)
                    }
                ) {
                    Checkbox(
                        checked = estado.recordarCorreo,
                        onCheckedChange = { viewModel.onRecordarCorreoChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AlbaOrange,
                            uncheckedColor = AlbaTextSecondary,
                            checkmarkColor = AlbaBlack
                        )
                    )
                    Text(
                        text = "Recordarme",
                        color = AlbaTextSecondary,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = AlbaOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            correoRecuperacion = estado.correoLogin
                            errorCorreoRecuperacion = null
                            mensajeRecuperacionEnviado = false
                            mostrarDialogoOlvido = true
                        }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // boton principal que ejecuta la validacion en el viewmodel antes de navegar al inicio
            BotonPrincipal(
                texto = "INGRESAR",
                alHacerClick = {
                    if (viewModel.validarLogin()) {
                        alIngresar()
                    }
                }
            )
        }
    }
}