package com.duoc.corpaliv.ui.components

// piezas visuales reutilizables de la app: barra superior con chip de usuario,
// barra inferior con pildora expandible, menu lateral renovado y botones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalAlternarTema
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro

// barra superior con boton de menu, titulo centrado y chip de sesion activa a la derecha
@Composable
fun AppTopBar(
    titulo: String = "",
    nombreUsuario: String = "Colaborador",
    alVolver: (() -> Unit)? = null,
    alAbrirMenu: (() -> Unit)? = null,
    alIrAPerfil: (() -> Unit)? = null
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
                .height(62.dp)
                .padding(horizontal = 16.dp),
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
                Spacer(modifier = Modifier.width(44.dp))
            }

            Text(
                text = tituloVisible.uppercase(),
                color = AlbaTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // chip informativo con punto verde de sesion activa y nombre corto del usuario
            ChipSesionActiva(
                nombre = nombreUsuario.ifBlank { "Colaborador" }
            )
        }
        HorizontalDivider(color = AlbaBorder, thickness = 1.dp)
    }
}

// insignia compacta en la barra superior que muestra quien ingreso al sistema
@Composable
private fun ChipSesionActiva(nombre: String) {
    Row(
        modifier = Modifier
            .widthIn(max = 124.dp)
            .clip(AlbaShapes.chip)
            .background(AlbaCard)
            .border(1.dp, AlbaBorder, AlbaShapes.chip)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(AlbaGreen)
        )
        Text(
            text = nombre,
            color = AlbaWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// foto circular de perfil usada en la cabecera de perfilscreen
@Composable
fun AvatarColaborador(
    tamano: Int = 36,
    alHacerClick: (() -> Unit)? = null
) {
    val esOscuro = LocalEsTemaOscuro.current
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .clip(CircleShape)
            .background(if (esOscuro) Color(0xFF3A332C) else Color(0xFFF2E6DF))
            .border(1.5.dp, AlbaOrange.copy(alpha = 0.5f), CircleShape)
            .then(if (alHacerClick != null) Modifier.clickable { alHacerClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = if (esOscuro) Color(0xFFD7B19D) else Color(0xFF8D6853),
            modifier = Modifier.size((tamano * 0.56f).dp)
        )
    }
}

// barra inferior flotante con 4 pestanas y animacion de pildora expandible
@Composable
fun AppBottomBar(
    rutaActual: String,
    alNavegar: (Screen) -> Unit
) {
    val esOscuro = LocalEsTemaOscuro.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbaBlack)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .shadow(
                    elevation = if (esOscuro) 2.dp else 8.dp,
                    shape = AlbaShapes.barraFlotante,
                    ambientColor = Color.Black.copy(alpha = 0.14f),
                    spotColor = Color.Black.copy(alpha = 0.14f)
                )
                .clip(AlbaShapes.barraFlotante)
                .background(AlbaCard)
                .border(1.dp, AlbaBorder, AlbaShapes.barraFlotante)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemBarraExpandible(
                etiqueta = "Inicio",
                icono = Icons.Default.Home,
                seleccionado = rutaActual == Screen.Home.route,
                alHacerClick = { alNavegar(Screen.Home) }
            )
            ItemBarraExpandible(
                etiqueta = "Catálogo",
                icono = Icons.Default.GridView,
                seleccionado = rutaActual == Screen.Catalogo.route,
                alHacerClick = { alNavegar(Screen.Catalogo) }
            )
            ItemBarraExpandible(
                etiqueta = "Contacto",
                icono = Icons.Default.Email,
                seleccionado = rutaActual == Screen.Contacto.route,
                alHacerClick = { alNavegar(Screen.Contacto) }
            )
            ItemBarraExpandible(
                etiqueta = "Perfil",
                icono = Icons.Default.Person,
                seleccionado = rutaActual == Screen.Perfil.route,
                alHacerClick = { alNavegar(Screen.Perfil) }
            )
        }
    }
}

// boton individual que se expande en pildora con texto solo cuando esta activo
@Composable
private fun ItemBarraExpandible(
    etiqueta: String,
    icono: ImageVector,
    seleccionado: Boolean,
    alHacerClick: () -> Unit
) {
    val esOscuro = LocalEsTemaOscuro.current
    val fondoActivo = if (esOscuro) AlbaOrange.copy(alpha = 0.22f) else AlbaOrange.copy(alpha = 0.14f)
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) fondoActivo else Color.Transparent,
        animationSpec = tween(durationMillis = 260),
        label = "fondoItemBottom"
    )
    val colorBorde by animateColorAsState(
        targetValue = if (seleccionado) AlbaOrange.copy(alpha = 0.35f) else Color.Transparent,
        animationSpec = tween(durationMillis = 260),
        label = "bordeItemBottom"
    )
    val colorContenido by animateColorAsState(
        targetValue = if (seleccionado) AlbaOrange else AlbaTextSecondary,
        animationSpec = tween(durationMillis = 240),
        label = "colorItemBottom"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clip(AlbaShapes.chip)
            .background(colorFondo)
            .border(1.dp, colorBorde, AlbaShapes.chip)
            // bloquea re-clicks si el usuario ya esta en esta pestana
            .clickable(
                enabled = !seleccionado,
                interactionSource = interactionSource,
                indication = null,
                onClick = alHacerClick
            )
            .padding(horizontal = if (seleccionado) 16.dp else 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = etiqueta,
            tint = colorContenido,
            modifier = Modifier.size(21.dp)
        )
        AnimatedVisibility(
            visible = seleccionado,
            enter = expandHorizontally(
                animationSpec = spring(
                    dampingRatio = 0.78f,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(tween(220)),
            exit = shrinkHorizontally(
                animationSpec = tween(180)
            ) + fadeOut(tween(140))
        ) {
            Row {
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = etiqueta,
                    color = colorContenido,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

// menu lateral con resaltado de ruta activa y liquid toggle para el modo oscuro
@Composable
fun AppDrawerContent(
    rutaActual: String = "",
    nombreUsuario: String = "Colaborador",
    correoUsuario: String = "",
    alCerrarMenu: () -> Unit,
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
            .width(304.dp)
            .border(1.dp, AlbaBorder, AlbaShapes.drawer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // cabecera con marca e identidad de sesion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ALBA LAB",
                        color = AlbaWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (correoUsuario.isNotBlank()) correoUsuario else "Fundación CORPALIV",
                        color = AlbaTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AlbaCard)
                        .border(1.dp, AlbaBorder, CircleShape)
                        .clickable { alCerrarMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar menú",
                        tint = AlbaWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = AlbaBorder)

            Spacer(modifier = Modifier.height(10.dp))

            // opciones de navegacion con bloqueo de re-click cuando ya estas en esa pantalla
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

            Spacer(modifier = Modifier.weight(1f))

            // tarjeta dedicada para alternar modo oscuro con liquid toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(AlbaShapes.tarjeta)
                    .background(AlbaCard)
                    .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
                    .clickable { alAlternarTemaDrawer() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (esOscuroDrawer) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        contentDescription = "Modo Oscuro",
                        tint = AlbaOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Modo Oscuro",
                            color = AlbaWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (esOscuroDrawer) "Activado" else "Desactivado",
                            color = AlbaTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                LiquidToggle(
                    checked = esOscuroDrawer,
                    onCheckedChange = { alAlternarTemaDrawer() }
                )
            }

            HorizontalDivider(color = AlbaBorder)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { alCerrarSesion() }
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    tint = AlbaOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = AlbaOrange,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// opcion individual del menu lateral que resalta la pantalla actual
@Composable
private fun ItemMenuLateral(
    texto: String,
    icono: ImageVector,
    activo: Boolean,
    alHacerClick: () -> Unit
) {
    val fondo = if (activo) AlbaOrange.copy(alpha = 0.14f) else Color.Transparent
    val colorTexto = if (activo) AlbaOrange else AlbaWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp)
            .clip(AlbaShapes.boton)
            .background(fondo)
            .clickable { alHacerClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = texto,
            tint = colorTexto,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = texto,
            color = colorTexto,
            fontSize = 15.sp,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium
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
