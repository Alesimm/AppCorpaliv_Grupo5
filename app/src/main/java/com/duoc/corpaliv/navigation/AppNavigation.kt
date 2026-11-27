package com.duoc.corpaliv.navigation

// mapa central de rutas con scaffold persistente, bloqueo de re-click y transiciones minimalistas
// evita destellos blancos y mantiene fija la barra inferior para animar la pildora en vivo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppDrawerContent
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.screens.AcercaDeScreen
import com.duoc.corpaliv.ui.screens.CatalogScreen
import com.duoc.corpaliv.ui.screens.ContactScreen
import com.duoc.corpaliv.ui.screens.DetalleScreen
import com.duoc.corpaliv.ui.screens.EstadoScreen
import com.duoc.corpaliv.ui.screens.HomeScreen
import com.duoc.corpaliv.ui.screens.LoginScreen
import com.duoc.corpaliv.ui.screens.PerfilScreen
import com.duoc.corpaliv.ui.screens.ResumenScreen
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.LocalEsTemaOscuro
import androidx.compose.ui.graphics.Color
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.MainViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

// titulo mostrado en la barra superior persistente segun la pantalla activa
private fun tituloParaRuta(ruta: String): String = when (ruta) {
    Screen.Home.route -> "ALBA LAB"
    Screen.Catalogo.route -> "CATÁLOGO"
    Screen.Favoritos.route -> "FAVORITOS"
    Screen.Contacto.route -> "CONTACTO"
    Screen.Resumen.route -> "SOLICITUD"
    Screen.AcercaDe.route -> "ACERCA DE"
    Screen.Perfil.route -> "CUENTA"
    Screen.Estado.route -> "CONFIGURACIÓN"
    else -> "ALBA LAB"
}

// contenedor principal con menu lateral izquierdo y scaffold persistente
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = viewModel(),
    estadoViewModel: EstadoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val sesion by usuarioViewModel.sesionActiva.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route ?: Screen.Login.route

    // solo login y detalle ocultan las barras globales
    val mostrarBarras = rutaActual != Screen.Login.route && rutaActual != Screen.Detalle.route

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // escucha ordenes de navegacion con guardia anti re-click para no recargar la misma pantalla
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { evento ->
            val actual = navController.currentDestination?.route
            when (evento) {
                is NavigationEvent.NavigateTo -> {
                    if (actual == evento.screen.route) return@collect
                    if (evento.screen == Screen.Home) {
                        if (!navController.popBackStack(Screen.Home.route, false)) {
                            navController.navigate(Screen.Home.route) { launchSingleTop = true }
                        }
                    } else {
                        navController.navigate(evento.screen.route) {
                            launchSingleTop = true
                        }
                    }
                }
                is NavigationEvent.NavigateBottom -> {
                    if (actual == evento.screen.route) return@collect
                    if (evento.screen == Screen.Home) {
                        if (!navController.popBackStack(Screen.Home.route, false)) {
                            navController.navigate(Screen.Home.route) { launchSingleTop = true }
                        }
                    } else {
                        navController.navigate(evento.screen.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                }
                is NavigationEvent.LoginExitoso -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                is NavigationEvent.CerrarSesion -> {
                    usuarioViewModel.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                is NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val abrirMenu: () -> Unit = remember(scope, drawerState) {
        { scope.launch { drawerState.open() } }
    }

    val cerrarMenuYNavegar: (Screen) -> Unit = remember(scope, drawerState, mainViewModel) {
        { destino ->
            scope.launch {
                drawerState.close()
                mainViewModel.navegarA(destino)
            }
        }
    }

    val esOscuroNav = LocalEsTemaOscuro.current
    // scrim suave en modo oscuro para atenuar ligeramente el fondo sin volverlo negro puro
    val colorScrimDrawer = if (esOscuroNav) {
        Color.Black.copy(alpha = 0.16f)
    } else {
        Color.Black.copy(alpha = 0.32f)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = colorScrimDrawer,
        gesturesEnabled = rutaActual != Screen.Login.route,
        drawerContent = {
            AppDrawerContent(
                rutaActual = rutaActual,
                nombreUsuario = sesion.nombre,
                correoUsuario = sesion.correo,
                alCerrarMenu = { scope.launch { drawerState.close() } },
                alIrACuenta = { cerrarMenuYNavegar(Screen.Perfil) },
                alIrAFavoritos = { cerrarMenuYNavegar(Screen.Favoritos) },
                alIrAConfiguracion = { cerrarMenuYNavegar(Screen.Estado) },
                alIrAAcercaDe = { cerrarMenuYNavegar(Screen.AcercaDe) },
                alCerrarSesion = {
                    scope.launch {
                        drawerState.close()
                        mainViewModel.cerrarSesion()
                    }
                }
            )
        }
    ) {
        // scaffold unico y persistente: las barras entran con animacion suave al salir del login
        Scaffold(
            containerColor = AlbaBlack,
            topBar = {
                AnimatedVisibility(
                    visible = mostrarBarras,
                    enter = slideInVertically(tween(520, easing = EaseOutCubic)) { -it } +
                        fadeIn(tween(460, easing = EaseOutCubic)),
                    exit = slideOutVertically(tween(240, easing = LinearOutSlowInEasing)) { -it } +
                        fadeOut(tween(200))
                ) {
                    AppTopBar(
                        titulo = tituloParaRuta(rutaActual),
                        alAbrirMenu = abrirMenu
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = mostrarBarras,
                    enter = slideInVertically(tween(580, delayMillis = 80, easing = EaseOutCubic)) { it } +
                        fadeIn(tween(520, delayMillis = 80, easing = EaseOutCubic)),
                    exit = slideOutVertically(tween(240, easing = LinearOutSlowInEasing)) { it } +
                        fadeOut(tween(200))
                ) {
                    AppBottomBar(
                        rutaActual = rutaActual,
                        alNavegar = { destino -> mainViewModel.navegarBottom(destino) }
                    )
                }
            }
        ) { innerPadding ->
            // transiciones minimalistas tipo react ligeramente mas largas para apreciar la fluidez
            val entradaSutil = fadeIn(tween(380, easing = EaseOutCubic)) +
                slideInVertically(tween(400, easing = EaseOutCubic)) { it / 24 }
            val salidaSutil = fadeOut(tween(220, easing = LinearOutSlowInEasing))

            val entradaDetalle = fadeIn(tween(360, easing = EaseOutCubic)) +
                slideInHorizontally(tween(420, easing = EaseOutCubic)) { it / 10 }
            val salidaDetalle = fadeOut(tween(240, easing = LinearOutSlowInEasing)) +
                slideOutHorizontally(tween(360, easing = EaseOutCubic)) { it / 10 }

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier
                    .fillMaxSize()
                    .background(AlbaBlack)
                    .then(
                        if (mostrarBarras) {
                            Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                                .imePadding()
                        } else {
                            Modifier
                        }
                    ),
                enterTransition = { entradaSutil },
                exitTransition = { salidaSutil },
                popEnterTransition = { entradaSutil },
                popExitTransition = { salidaSutil }
            ) {
                composable(
                    route = Screen.Login.route,
                    enterTransition = { fadeIn(tween(360)) },
                    exitTransition = {
                        fadeOut(tween(460, easing = EaseOutCubic)) +
                            scaleOut(targetScale = 1.03f, animationSpec = tween(460, easing = EaseOutCubic))
                    }
                ) {
                    LoginScreen(
                        viewModel = usuarioViewModel,
                        alIngresar = { mainViewModel.ingresarAlHome() }
                    )
                }

                composable(
                    route = Screen.Home.route,
                    enterTransition = {
                        if (initialState.destination.route == Screen.Login.route) {
                            fadeIn(tween(680, easing = EaseOutCubic)) +
                                scaleIn(initialScale = 0.94f, animationSpec = tween(680, easing = EaseOutCubic)) +
                                slideInVertically(tween(680, easing = EaseOutCubic)) { it / 28 }
                        } else {
                            entradaSutil
                        }
                    }
                ) {
                    HomeScreen(
                        usuarioViewModel = usuarioViewModel,
                        alSeleccionarPrograma = { programa ->
                            usuarioViewModel.seleccionarPrograma(programa)
                            mainViewModel.navegarA(Screen.Detalle)
                        },
                        alSeleccionarColaboracion = { tipo ->
                            usuarioViewModel.preseleccionarColaboracion(tipo)
                            mainViewModel.navegarBottom(Screen.Contacto)
                        }
                    )
                }

                composable(Screen.Catalogo.route) {
                    CatalogScreen(
                        estadoViewModel = estadoViewModel,
                        soloFavoritos = false,
                        alSeleccionarPrograma = { programa ->
                            usuarioViewModel.seleccionarPrograma(programa)
                            mainViewModel.navegarA(Screen.Detalle)
                        }
                    )
                }

                composable(Screen.Favoritos.route) {
                    CatalogScreen(
                        estadoViewModel = estadoViewModel,
                        soloFavoritos = true,
                        alSeleccionarPrograma = { programa ->
                            usuarioViewModel.seleccionarPrograma(programa)
                            mainViewModel.navegarA(Screen.Detalle)
                        }
                    )
                }

                composable(
                    route = Screen.Detalle.route,
                    enterTransition = { entradaDetalle },
                    popExitTransition = { salidaDetalle }
                ) {
                    DetalleScreen(
                        usuarioViewModel = usuarioViewModel,
                        estadoViewModel = estadoViewModel,
                        alVolver = { mainViewModel.volverAtras() },
                        alQuieroColaborar = { mainViewModel.navegarBottom(Screen.Contacto) }
                    )
                }

                composable(Screen.Contacto.route) {
                    ContactScreen(
                        viewModel = usuarioViewModel,
                        estadoViewModel = estadoViewModel,
                        alIrAResumen = { mainViewModel.navegarA(Screen.Resumen) }
                    )
                }

                composable(
                    route = Screen.Resumen.route,
                    enterTransition = { entradaDetalle },
                    popExitTransition = { salidaDetalle }
                ) {
                    ResumenScreen(
                        viewModel = usuarioViewModel,
                        alIrAInicio = { mainViewModel.navegarBottom(Screen.Home) },
                        alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) }
                    )
                }

                composable(Screen.AcercaDe.route) {
                    AcercaDeScreen()
                }

                composable(Screen.Perfil.route) {
                    PerfilScreen(
                        usuarioViewModel = usuarioViewModel,
                        estadoViewModel = estadoViewModel,
                        alVerSolicitud = { mainViewModel.navegarA(Screen.Resumen) },
                        alIrAFavoritos = { mainViewModel.navegarA(Screen.Favoritos) }
                    )
                }

                composable(Screen.Estado.route) {
                    EstadoScreen(
                        alVolver = { mainViewModel.navegarBottom(Screen.Home) },
                        viewModel = estadoViewModel
                    )
                }
            }
        }
    }
}
