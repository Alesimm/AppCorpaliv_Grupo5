package com.duoc.corpaliv.ui.components

// piezas visuales reutilizables de la app: barra superior con chip de sesion,
// barra inferior con pildora expandible fiel al video y menu lateral minimalista a la izquierda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaError
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalAlternarTema
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro

// barra superior minimalista con boton de menu y titulo perfectamente centrado
@Composable
fun AppTopBar(
    titulo: String = "",
    alVolver: (() -> Unit)? = null,
    alAbrirMenu: (() -> Unit)? = null
) {
    val tituloVisible = titulo.ifBlank { "ALBA LAB" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbaBlack)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (alAbrirMenu != null) {
                IconButton(onClick = alAbrirMenu) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = AlbaWhite
                    )
                }
            } else if (alVolver != null) {
                TextButton(onClick = alVolver) {
                    Text(text = "← Volver", color = AlbaWhite)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            Text(
                text = tituloVisible.uppercase(),
                color = AlbaTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 2.8.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // espacio equilibrado a la derecha para mantener el titulo centrado y limpio
            Spacer(modifier = Modifier.size(48.dp))
        }
        HorizontalDivider(color = AlbaBorder, thickness = 1.dp)
    }
}

// avatar circular con degradado institucional de corpaliv usado en sidebar y cuenta
@Composable
fun AvatarColaborador(
    tamano: Int = 72,
    alHacerClick: (() -> Unit)? = null
) {
    val degradado = remember {
        Brush.linearGradient(
            colors = listOf(AlbaOrange, AlbaPurple)
        )
    }
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .clip(CircleShape)
            .background(degradado)
            .then(if (alHacerClick != null) Modifier.clickable { alHacerClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Cuenta",
            tint = Color.White,
            modifier = Modifier.size((tamano * 0.52f).dp)
        )
    }
}

// barra inferior flotante de 3 pestanas con animacion fiel al video expanding label
@Composable
fun AppBottomBar(
    rutaActual: String,
    alNavegar: (Screen) -> Unit
) {
    val esOscuro = LocalEsTemaOscuro.current
    val formaBarra = AlbaShapes.barraFlotante

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbaBlack)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = if (esOscuro) 4.dp else 10.dp,
                    shape = formaBarra,
                    ambientColor = Color.Black.copy(alpha = 0.16f),
                    spotColor = Color.Black.copy(alpha = 0.16f)
                )
                .clip(formaBarra)
                .background(AlbaCard)
                .border(1.dp, AlbaBorder, formaBarra)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemBarraExpandible(
                etiqueta = "Inicio",
                iconoInactivo = Icons.Outlined.Home,
                iconoActivo = Icons.Filled.Home,
                seleccionado = rutaActual == Screen.Home.route,
                alHacerClick = { alNavegar(Screen.Home) }
            )
            ItemBarraExpandible(
                etiqueta = "Catálogo",
                iconoInactivo = Icons.Outlined.GridView,
                iconoActivo = Icons.Filled.GridView,
                seleccionado = rutaActual == Screen.Catalogo.route,
                alHacerClick = { alNavegar(Screen.Catalogo) }
            )
            ItemBarraExpandible(
                etiqueta = "Contacto",
                iconoInactivo = Icons.Outlined.Email,
                iconoActivo = Icons.Filled.Email,
                seleccionado = rutaActual == Screen.Contacto.route,
                alHacerClick = { alNavegar(Screen.Contacto) }
            )
        }
    }
}

// pildora organica con elevacion propia que se expande y contrae suavemente al cambiar de pestana
@Composable
private fun ItemBarraExpandible(
    etiqueta: String,
    iconoInactivo: ImageVector,
    iconoActivo: ImageVector,
    seleccionado: Boolean,
    alHacerClick: () -> Unit
) {
    val esOscuro = LocalEsTemaOscuro.current
    val fondoActivo = if (esOscuro) Color(0xFF2C1E14) else Color(0xFFFFEFE4)
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) fondoActivo else Color.Transparent,
        animationSpec = tween(durationMillis = 320),
        label = "fondoItemBottom"
    )
    val colorContenido by animateColorAsState(
        targetValue = if (seleccionado) AlbaOrange else AlbaTextSecondary,
        animationSpec = tween(durationMillis = 280),
        label = "colorItemBottom"
    )
    val elevacionPildora by animateDpAsState(
        targetValue = if (seleccionado) (if (esOscuro) 2.dp else 6.dp) else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "elevacionItemBottom"
    )
    val paddingHorizontal by animateDpAsState(
        targetValue = if (seleccionado) 18.dp else 14.dp,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "paddingItemBottom"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .shadow(
                elevation = elevacionPildora,
                shape = AlbaShapes.chip,
                ambientColor = AlbaOrange.copy(alpha = 0.25f),
                spotColor = AlbaOrange.copy(alpha = 0.28f)
            )
            .clip(AlbaShapes.chip)
            .background(colorFondo)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.76f,
                    stiffness = Spring.StiffnessLow
                )
            )
            // bloquea re-clicks cuando el usuario ya esta en esta pantalla
            .clickable(
                enabled = !seleccionado,
                interactionSource = interactionSource,
                indication = null,
                onClick = alHacerClick
            )
            .padding(horizontal = paddingHorizontal, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (seleccionado) iconoActivo else iconoInactivo,
            contentDescription = etiqueta,
            tint = colorContenido,
            modifier = Modifier.size(21.dp)
        )
        AnimatedVisibility(
            visible = seleccionado,
            enter = expandHorizontally(
                animationSpec = spring(
                    dampingRatio = 0.76f,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(tween(260)),
            exit = shrinkHorizontally(
                animationSpec = tween(220)
            ) + fadeOut(tween(150))
        ) {
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = etiqueta,
                    color = colorContenido,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

// menu lateral izquierdo minimalista inspirado en la referencia:
// avatar centrado con nombre y correo arriba, luego cuenta, opciones, modo oscuro con toggle y cerrar sesion
@Composable
fun AppDrawerContent(
    rutaActual: String = "",
    nombreUsuario: String = "Colaborador",
    correoUsuario: String = "",
    alCerrarMenu: () -> Unit,
    alIrACuenta: () -> Unit,
    alIrAFavoritos: () -> Unit,
    alIrAConfiguracion: () -> Unit,
    alIrAAcercaDe: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val esOscuroDrawer = LocalEsTemaOscuro.current
    val alAlternarTemaDrawer = LocalAlternarTema.current

    ModalDrawerSheet(
        drawerContainerColor = AlbaBlack,
        drawerContentColor = AlbaWhite,
        drawerShape = AlbaShapes.drawer,
        modifier = Modifier
            .fillMaxHeight()
            .width(292.dp)
            .border(1.dp, AlbaBorder, AlbaShapes.drawer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // bloque superior centrado con foto/icono, nombre de usuario y correo
            Spacer(modifier = Modifier.height(8.dp))

            AvatarColaborador(
                tamano = 76,
                alHacerClick = {
                    if (rutaActual == Screen.Perfil.route) alCerrarMenu() else alIrACuenta()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = nombreUsuario.ifBlank { "Colaborador" },
                color = AlbaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (correoUsuario.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = correoUsuario,
                    color = AlbaTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = AlbaBorder,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // lista limpia y minimalista con bloqueo de re-click en la pantalla activa
            ItemMenuLateral(
                texto = "Cuenta",
                icono = Icons.Outlined.Person,
                activo = rutaActual == Screen.Perfil.route,
                alHacerClick = {
                    if (rutaActual == Screen.Perfil.route) alCerrarMenu() else alIrACuenta()
                }
            )

            ItemMenuLateral(
                texto = "Mis Favoritos",
                icono = Icons.Default.FavoriteBorder,
                activo = rutaActual == Screen.Favoritos.route,
                alHacerClick = {
                    if (rutaActual == Screen.Favoritos.route) alCerrarMenu() else alIrAFavoritos()
                }
            )

            ItemMenuLateral(
                texto = "Configuración",
                icono = Icons.Default.Settings,
                activo = rutaActual == Screen.Estado.route,
                alHacerClick = {
                    if (rutaActual == Screen.Estado.route) alCerrarMenu() else alIrAConfiguracion()
                }
            )

            ItemMenuLateral(
                texto = "Acerca de CORPALIV",
                icono = Icons.Default.Info,
                activo = rutaActual == Screen.AcercaDe.route,
                alHacerClick = {
                    if (rutaActual == Screen.AcercaDe.route) alCerrarMenu() else alIrAAcercaDe()
                }
            )

            // fila limpia de modo oscuro con liquid toggle alineado a la derecha como en la foto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AlbaShapes.boton)
                    .clickable { alAlternarTemaDrawer() }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = if (esOscuroDrawer) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                        contentDescription = "Modo Oscuro",
                        tint = AlbaTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Modo Oscuro",
                        color = AlbaWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                LiquidToggle(
                    checked = esOscuroDrawer,
                    onCheckedChange = { alAlternarTemaDrawer() },
                    ancho = 48.dp,
                    alto = 26.dp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // cerrar sesion en tono acento suave rojo/coral como en la referencia
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AlbaShapes.boton)
                    .clickable { alCerrarSesion() }
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    tint = AlbaError,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = AlbaError,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// fila limpia del menu lateral que resalta la ruta activa sin recargarla al volver a tocar
@Composable
private fun ItemMenuLateral(
    texto: String,
    icono: ImageVector,
    activo: Boolean,
    alHacerClick: () -> Unit
) {
    val fondo = if (activo) AlbaOrange.copy(alpha = 0.12f) else Color.Transparent
    val colorIcono = if (activo) AlbaOrange else AlbaTextSecondary
    val colorTexto = if (activo) AlbaOrange else AlbaWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(AlbaShapes.boton)
            .background(fondo)
            .clickable { alHacerClick() }
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = texto,
            tint = colorIcono,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = texto,
            color = colorTexto,
            fontSize = 15.sp,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

// boton principal con esquinas redondeadas unificadas
@Composable
fun BotonPrincipal(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorFondo: Color = AlbaWhite,
    colorTexto: Color = AlbaBlack
) {
    Button(
        onClick = alHacerClick,
        shape = AlbaShapes.boton,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorTexto
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = texto.uppercase(),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 13.sp
        )
    }
}

// boton secundario con borde fino y esquinas redondeadas
@Composable
fun BotonSecundario(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = alHacerClick,
        shape = AlbaShapes.boton,
        border = BorderStroke(1.dp, AlbaBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AlbaCard,
            contentColor = AlbaWhite
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = texto.uppercase(),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 13.sp
        )
    }
}

// tarjeta desplegable para mostrar encargados, correos y llamar directo
@Composable
fun TarjetaContactoDesplegable(
    area: String,
    encargado: String,
    telefono: String,
    correo: String,
    alLlamar: (String) -> Unit
) {
    var desplegado by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
            .clip(AlbaShapes.tarjeta)
            .clickable { desplegado = !desplegado },
        shape = AlbaShapes.tarjeta,
        colors = CardDefaults.cardColors(containerColor = AlbaCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = area,
                    color = AlbaWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (desplegado) "▲" else "▼",
                    color = AlbaOrange,
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(visible = desplegado) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = AlbaBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Encargado: $encargado", color = AlbaTextSecondary, fontSize = 13.sp)
                    Text(text = "Correo: $correo", color = AlbaTextSecondary, fontSize = 13.sp)
                    Text(text = "Teléfono: $telefono", color = AlbaTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    BotonSecundario(
                        texto = "LLAMAR AHORA",
                        alHacerClick = { alLlamar(telefono) }
                    )
                }
            }
        }
    }
}
