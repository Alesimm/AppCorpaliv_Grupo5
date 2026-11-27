package com.duoc.corpaliv.ui.screens

// pantalla del formulario para enviar una propuesta de ayuda o alianza
// agrupa los campos en dos tarjetas minimalistas con valores uf/utm/dolar desplegables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.ui.components.OpcionSelector
import com.duoc.corpaliv.ui.components.SelectorBottomSheetAlba
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaSurface
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import com.duoc.corpaliv.ui.utils.NotificacionHelper
import com.duoc.corpaliv.ui.utils.hayConexionInternet
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.IndicadoresUiState
import com.duoc.corpaliv.viewmodel.IndicadoresViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

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

// formulario de contacto dividido en dos tarjetas limpias y referencia uf desplegable
@Composable
fun ContactScreen(
    viewModel: UsuarioViewModel,
    estadoViewModel: EstadoViewModel,
    alIrAResumen: () -> Unit = {},
    indicadoresViewModel: IndicadoresViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val modoColaboradorActivo by estadoViewModel.activo.collectAsStateWithLifecycle()
    val estadoIndicadores by indicadoresViewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mensajeBringIntoView = remember { BringIntoViewRequester() }
    var indicadoresAbiertos by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Contacto",
                color = AlbaWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Completa tus datos y selecciona cómo quieres apoyar.",
                color = AlbaTextSecondary,
                fontSize = 14.sp
            )
        }

        // tarjeta 1: datos del colaborador sin placeholders de ejemplo
        TarjetaSeccionContacto(titulo = "Datos de contacto") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    EtiquetaCampo("NOMBRE")
                    OutlinedTextField(
                        value = estado.nombre,
                        onValueChange = viewModel::onNombreChange,
                        placeholder = { Text("Tu nombre", color = AlbaTextSecondary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AlbaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        isError = estado.errores.nombre != null,
                        supportingText = {
                            estado.errores.nombre?.let { Text(text = it, color = AlbaError, fontSize = 11.sp) }
                        },
                        shape = AlbaShapes.campo,
                        colors = coloresInputContacto(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    EtiquetaCampo("APELLIDO")
                    OutlinedTextField(
                        value = estado.apellido,
                        onValueChange = viewModel::onApellidoChange,
                        placeholder = { Text("Tu apellido", color = AlbaTextSecondary, fontSize = 14.sp) },
                        singleLine = true,
                        isError = estado.errores.apellido != null,
                        supportingText = {
                            estado.errores.apellido?.let { Text(text = it, color = AlbaError, fontSize = 11.sp) }
                        },
                        shape = AlbaShapes.campo,
                        colors = coloresInputContacto(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            EtiquetaCampo("CORREO ELECTRÓNICO")
            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                placeholder = { Text("Tu correo electrónico", color = AlbaTextSecondary, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = AlbaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let { Text(text = it, color = AlbaError, fontSize = 11.sp) }
                },
                shape = AlbaShapes.campo,
                colors = coloresInputContacto(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // tarjeta 2: modalidad de colaboracion, referencia uf desplegable, programa y mensaje
        TarjetaSeccionContacto(titulo = "Tu propuesta") {
            EtiquetaCampo("¿CÓMO QUIERES AYUDAR?")
            SelectorBottomSheetAlba(
                tituloSheet = "¿Cómo quieres ayudar?",
                valorActual = estado.colaboracion,
                placeholder = "Selecciona una modalidad",
                opciones = OPCIONES_COLABORACION,
                alSeleccionar = viewModel::onColaboracionChange,
                esError = estado.errores.colaboracion != null,
                fondoCampo = AlbaSurface
            )
            estado.errores.colaboracion?.let {
                Text(
                    text = it,
                    color = AlbaError,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            // desplegable minimalista de referencia uf, utm y dolar sin ruido visual
            Spacer(modifier = Modifier.height(10.dp))
            DesplegableIndicadoresContacto(
                abierto = indicadoresAbiertos,
                alAlternar = { indicadoresAbiertos = !indicadoresAbiertos },
                estado = estadoIndicadores,
                alRecargar = { indicadoresViewModel.cargarIndicadores(manual = true) }
            )

            AnimatedVisibility(visible = estado.colaboracion == "Alianza empresarial") {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    EtiquetaCampo("RUT COMERCIAL")
                    OutlinedTextField(
                        value = estado.rutComercial,
                        onValueChange = viewModel::onRutComercialChange,
                        placeholder = { Text("Sin puntos y con guion", color = AlbaTextSecondary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = AlbaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        isError = estado.errores.rutComercial != null,
                        supportingText = {
                            estado.errores.rutComercial?.let { Text(text = it, color = AlbaError, fontSize = 11.sp) }
                        },
                        shape = AlbaShapes.campo,
                        colors = coloresInputContacto(),
                        modifier = Modifier.fillMaxWidth()
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
                alSeleccionar = viewModel::onProgramaFormChange,
                fondoCampo = AlbaSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            EtiquetaCampo("MENSAJE ADICIONAL (OPCIONAL)")
            OutlinedTextField(
                value = estado.mensaje,
                onValueChange = {
                    viewModel.onMensajeChange(it)
                    scope.launch { mensajeBringIntoView.bringIntoView() }
                },
                placeholder = { Text("Cuéntanos brevemente tu propuesta...", color = AlbaTextSecondary, fontSize = 14.sp) },
                minLines = 3,
                isError = estado.errores.mensaje != null,
                supportingText = {
                    estado.errores.mensaje?.let { Text(text = it, color = AlbaError, fontSize = 11.sp) }
                },
                shape = AlbaShapes.campo,
                colors = coloresInputContacto(),
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(mensajeBringIntoView)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            scope.launch {
                                delay(250)
                                mensajeBringIntoView.bringIntoView()
                            }
                        }
                    }
            )
        }

        val esOscuroBtn = LocalEsTemaOscuro.current
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
            border = if (esOscuroBtn) BorderStroke(1.dp, AlbaBorder) else null,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (esOscuroBtn) Color(0xFF34343D) else AlbaWhite,
                contentColor = if (esOscuroBtn) AlbaWhite else AlbaBlack
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

// contenedor tarjeta minimalista para agrupar campos relacionados del formulario
@Composable
private fun TarjetaSeccionContacto(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AlbaShapes.tarjeta)
            .background(AlbaCard)
            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            color = AlbaWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))
        contenido()
    }
}

// barra desplegable minimalista con referencia uf/utm/dolar para apoyar aportes y donaciones
@Composable
private fun DesplegableIndicadoresContacto(
    abierto: Boolean,
    alAlternar: () -> Unit,
    estado: IndicadoresUiState,
    alRecargar: () -> Unit
) {
    val rotacionFlecha by animateFloatAsState(
        targetValue = if (abierto) 180f else 0f,
        animationSpec = tween(220),
        label = "flecha_uf_contacto"
    )
    val transicionGiro = rememberInfiniteTransition(label = "giro_uf_contacto")
    val anguloGiro by transicionGiro.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angulo_uf_contacto"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AlbaShapes.campo)
            .background(AlbaSurface)
            .border(1.dp, if (abierto) AlbaOrange.copy(alpha = 0.55f) else AlbaBorder, AlbaShapes.campo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { alAlternar() }
                .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Referencia económica",
                    tint = AlbaOrange,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Valores de referencia (UF · UTM · Dólar)",
                    color = AlbaWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (abierto) "Ocultar valores" else "Ver valores",
                tint = AlbaTextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotacionFlecha)
            )
        }

        AnimatedVisibility(
            visible = abierto,
            enter = expandVertically(tween(240)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(160))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
            ) {
                HorizontalDivider(color = AlbaBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                if (estado.resumen != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DatoMinimalistaUf("UF", formatearPesosContacto(estado.resumen.uf), Modifier.weight(1f))
                        DatoMinimalistaUf("UTM", formatearPesosContacto(estado.resumen.utm), Modifier.weight(1f))
                        DatoMinimalistaUf("Dólar", formatearPesosContacto(estado.resumen.dolar), Modifier.weight(1f))
                    }
                } else {
                    Text(
                        text = if (estado.cargando) "Consultando mindicador.cl..." else "Sin conexión · toca recargar para reintentar.",
                        color = AlbaTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "mindicador.cl${if (!estado.resumen?.fecha.isNullOrBlank()) " · ${estado.resumen?.fecha}" else ""}",
                        color = AlbaTextSecondary,
                        fontSize = 11.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(AlbaShapes.chip)
                            .clickable(enabled = !estado.cargando) { alRecargar() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar indicadores",
                            tint = AlbaOrange,
                            modifier = Modifier
                                .size(15.dp)
                                .rotate(if (estado.cargando) anguloGiro else 0f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Actualizar",
                            color = AlbaOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                AnimatedVisibility(
                    visible = estado.mensajeEstado != null,
                    enter = fadeIn(tween(200)),
                    exit = fadeOut(tween(240))
                ) {
                    Text(
                        text = estado.mensajeEstado.orEmpty(),
                        color = if (estado.esError) AlbaOrange else AlbaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

// valor limpio dentro del desplegable sin cajas pesadas para mantener estetica minimalista
@Composable
private fun DatoMinimalistaUf(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = etiqueta,
            color = AlbaTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor,
            color = AlbaWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// formatea un numero decimal a pesos chilenos redondeados como $38.500
private fun formatearPesosContacto(valor: Double): String {
    val formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CL"))
    return "$${formato.format(valor.roundToLong())}"
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        color = AlbaTextSecondary,
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 5.dp)
    )
}

@Composable
private fun coloresInputContacto() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = AlbaSurface,
    unfocusedContainerColor = AlbaSurface,
    focusedBorderColor = AlbaOrange,
    unfocusedBorderColor = AlbaBorder,
    focusedTextColor = AlbaWhite,
    unfocusedTextColor = AlbaWhite
)
