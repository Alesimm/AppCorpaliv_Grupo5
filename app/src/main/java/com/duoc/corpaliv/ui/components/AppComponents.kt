package com.duoc.corpaliv.ui.components

// piezas visuales reutilizables que se usan en varias pantallas de la app
// contiene la barra superior, la barra inferior, el menu lateral y los botones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.theme.LocalAlternarTema
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro

// barra superior que muestra el titulo, el boton del menu y la foto de perfil
@Composable
fun AppTopBar(
    titulo: String = "",
    alVolver: (() -> Unit)? = null,
    alAbrirMenu: (() -> Unit)? = null,
    alIrAPerfil: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbaBlack)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp),
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
                Spacer(modifier = Modifier.width(40.dp))
            }

            if (titulo.isNotBlank()) {
                Text(
                    text = titulo.uppercase(),
                    color = AlbaTextSecondary,
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            if (alIrAPerfil != null) {
                AvatarColaborador(
                    tamano = 36,
                    alHacerClick = alIrAPerfil
                )
            } else {
                Spacer(modifier = Modifier.width(36.dp))
            }
        }
        HorizontalDivider(color = AlbaBorder, thickness = 1.dp)
    }
}

// foto circular de perfil del usuario que al tocarla abre su perfil
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
            .border(1.dp, AlbaBorder, CircleShape)
            .then(if (alHacerClick != null) Modifier.clickable { alHacerClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = if (esOscuro) Color(0xFFD7B19D) else Color(0xFF8D6853),
            modifier = Modifier.size((tamano * 0.6f).dp)
        )
    }
}

// barra de tres botones en la parte de abajo: inicio, catalogo y contacto
@Composable
fun AppBottomBar(
    rutaActual: String,
    alNavegar: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbaBlack)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = AlbaBorder, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemBarraInferior(
                etiqueta = "INICIO",
                icono = Icons.Default.Home,
                seleccionado = rutaActual == Screen.Home.route,
                alHacerClick = { alNavegar(Screen.Home) }
            )
            ItemBarraInferior(
                etiqueta = "CATÁLOGO",
                icono = Icons.Default.GridView,
                seleccionado = rutaActual == Screen.Catalogo.route,
                alHacerClick = { alNavegar(Screen.Catalogo) }
            )
            ItemBarraInferior(
                etiqueta = "CONTACTO",
                icono = Icons.Default.Email,
                seleccionado = rutaActual == Screen.Contacto.route,
                alHacerClick = { alNavegar(Screen.Contacto) }
            )
        }
    }
}

// boton individual de la barra inferior con su icono, texto y rayita naranja
@Composable
private fun ItemBarraInferior(
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    seleccionado: Boolean,
    alHacerClick: () -> Unit
) {
    val colorContenido = if (seleccionado) AlbaWhite else AlbaTextSecondary
    Column(
        modifier = Modifier
            .clickable { alHacerClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = etiqueta,
            tint = colorContenido,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = etiqueta,
            color = colorContenido,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(2.dp)
                .background(if (seleccionado) AlbaOrange else Color.Transparent)
        )
    }
}

// menu que se abre desde el lado izquierdo con accesos a favoritos y ajustes
@Composable
fun AppDrawerContent(
    alCerrarMenu: () -> Unit,
    alIrAFavoritos: () -> Unit,
    alIrAConfiguracion: () -> Unit,
    alIrAAcercaDe: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = AlbaBlack,
        drawerContentColor = AlbaWhite,
        drawerShape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .border(1.dp, AlbaBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ALBA LAB",
                    color = AlbaWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, AlbaBorder)
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

            ItemMenuLateral(
                texto = "Mis Favoritos",
                icono = Icons.Default.FavoriteBorder,
                alHacerClick = alIrAFavoritos
            )
            HorizontalDivider(color = AlbaBorder)

            ItemMenuLateral(
                texto = "Configuración",
                icono = Icons.Default.Settings,
                alHacerClick = alIrAConfiguracion
            )
            HorizontalDivider(color = AlbaBorder)

            ItemMenuLateral(
                texto = "Acerca de CORPALIV",
                icono = Icons.Default.Info,
                alHacerClick = alIrAAcercaDe
            )
            HorizontalDivider(color = AlbaBorder)

            val esOscuroDrawer = LocalEsTemaOscuro.current
            val alAlternarTemaDrawer = LocalAlternarTema.current
            ItemMenuLateral(
                texto = if (esOscuroDrawer) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
                icono = if (esOscuroDrawer) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                alHacerClick = alAlternarTemaDrawer
            )
            HorizontalDivider(color = AlbaBorder)

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(color = AlbaBorder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { alCerrarSesion() }
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    tint = AlbaOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = AlbaOrange,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// opcion dentro del menu lateral con su icono y texto explicativo
@Composable
private fun ItemMenuLateral(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    alHacerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alHacerClick() }
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = texto,
            tint = AlbaWhite,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            color = AlbaWhite,
            fontSize = 15.sp
        )
    }
}

// boton grande y blanco usado para las acciones principales como enviar o ingresar
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
        shape = RoundedCornerShape(2.dp),
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

// boton con borde fino usado para acciones secundarias como volver o llamar
@Composable
fun BotonSecundario(
    texto: String,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = alHacerClick,
        shape = RoundedCornerShape(2.dp),
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

// tarjeta que se toca para desplegar los telefonos y correos de contacto
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
            .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            .clickable { desplegado = !desplegado },
        shape = RoundedCornerShape(2.dp),
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