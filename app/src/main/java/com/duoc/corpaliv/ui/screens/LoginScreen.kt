package com.duoc.corpaliv.ui.screens

// pantalla de inicio de sesion con logo de corpaliv y dialogo minimalista de recuperacion
// al validar el correo de recuperacion el borde cambia a verde con ticket animado y mensaje limpio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duoc.corpaliv.R
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.DialogoAnimadoAlba
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import com.duoc.corpaliv.validation.validarCorreo
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// vista de acceso con validacion segura sin pistas de formato en credenciales
@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    alIngresar: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val esOscuro = LocalEsTemaOscuro.current

    var mostrarClave by remember { mutableStateOf(false) }
    var mostrarDialogoOlvido by remember { mutableStateOf(false) }
    var correoRecuperacion by remember { mutableStateOf("") }
    var errorCorreoRecuperacion by remember { mutableStateOf<String?>(null) }
    var mensajeRecuperacionEnviado by remember { mutableStateOf(false) }

    val escalaLogo = remember { Animatable(1.04f) }
    val alphaLogo = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alphaLogo.animateTo(1f, tween(380, easing = EaseOutCubic))
        escalaLogo.animateTo(1f, tween(420, easing = EaseOutCubic))
    }

    // dialogo minimalista animado para recuperar contrasena con borde verde y ticket al validar
    if (mostrarDialogoOlvido) {
        val bordeEnfocado by animateColorAsState(
            targetValue = when {
                errorCorreoRecuperacion != null -> AlbaError
                mensajeRecuperacionEnviado -> AlbaGreen
                else -> AlbaOrange
            },
            animationSpec = tween(280),
            label = "bordeRecEnfocado"
        )
        val bordeSinFoco by animateColorAsState(
            targetValue = when {
                errorCorreoRecuperacion != null -> AlbaError
                mensajeRecuperacionEnviado -> AlbaGreen
                else -> AlbaBorder
            },
            animationSpec = tween(280),
            label = "bordeRecNormal"
        )

        DialogoAnimadoAlba(
            alCerrar = {
                mostrarDialogoOlvido = false
                errorCorreoRecuperacion = null
                mensajeRecuperacionEnviado = false
            }
        ) {
            Text(
                text = "Recuperar contraseña",
                color = AlbaWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ingresa tu correo registrado para enviarte un enlace de recuperación.",
                color = AlbaTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correoRecuperacion,
                onValueChange = {
                    correoRecuperacion = it
                    errorCorreoRecuperacion = null
                    mensajeRecuperacionEnviado = false
                },
                placeholder = { Text("nombre@ejemplo.cl", color = AlbaTextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = if (mensajeRecuperacionEnviado) AlbaGreen else AlbaTextSecondary
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = mensajeRecuperacionEnviado,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(tween(200)),
                        exit = fadeOut(tween(150))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Correo enviado",
                            tint = AlbaGreen
                        )
                    }
                },
                isError = errorCorreoRecuperacion != null,
                supportingText = {
                    errorCorreoRecuperacion?.let { msg ->
                        Text(text = msg, color = AlbaError)
                    }
                },
                singleLine = true,
                shape = AlbaShapes.campo,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AlbaBlack,
                    unfocusedContainerColor = AlbaBlack,
                    focusedBorderColor = bordeEnfocado,
                    unfocusedBorderColor = bordeSinFoco,
                    focusedTextColor = AlbaWhite,
                    unfocusedTextColor = AlbaWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // banner minimalista verde que se despliega suavemente bajo el campo
            AnimatedVisibility(
                visible = mensajeRecuperacionEnviado,
                enter = expandVertically(tween(280, easing = EaseOutCubic)) + fadeIn(tween(260)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(150))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(AlbaShapes.campo)
                        .background(AlbaGreen.copy(alpha = 0.12f))
                        .border(1.dp, AlbaGreen.copy(alpha = 0.45f), AlbaShapes.campo)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = AlbaGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Listo. Enviamos las instrucciones de recuperación a tu correo.",
                        color = AlbaGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        mostrarDialogoOlvido = false
                        errorCorreoRecuperacion = null
                        mensajeRecuperacionEnviado = false
                    }
                ) {
                    Text("CERRAR", color = AlbaTextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        val error = validarCorreo(correoRecuperacion)
                        if (error != null) {
                            errorCorreoRecuperacion = error
                            mensajeRecuperacionEnviado = false
                        } else {
                            errorCorreoRecuperacion = null
                            mensajeRecuperacionEnviado = true
                        }
                    }
                ) {
                    Text(
                        text = if (mensajeRecuperacionEnviado) "REENVIAR" else "ENVIAR",
                        color = if (mensajeRecuperacionEnviado) AlbaGreen else AlbaOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(
                targetState = if (esOscuro) R.drawable.logo_corpaliv_oscuro else R.drawable.logo_corpaliv_claro,
                animationSpec = tween(320),
                label = "cambioLogo"
            ) { logoId ->
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = "Logo Fundación CORPALIV",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(285.dp)
                        .heightIn(min = 100.dp)
                        .graphicsLayer {
                            scaleX = escalaLogo.value
                            scaleY = escalaLogo.value
                            alpha = alphaLogo.value
                        }
                )
            }

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

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "CORREO",
                color = AlbaTextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

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
                        if (it.isNotBlank()) {
                            Text(text = it, color = AlbaError)
                        }
                    }
                },
                singleLine = true,
                shape = AlbaShapes.campo,
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

            OutlinedTextField(
                value = estado.claveLogin,
                onValueChange = viewModel::onClaveLoginChange,
                placeholder = { Text("Ingresa tu contraseña", color = AlbaTextSecondary) },
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
                shape = AlbaShapes.campo,
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
