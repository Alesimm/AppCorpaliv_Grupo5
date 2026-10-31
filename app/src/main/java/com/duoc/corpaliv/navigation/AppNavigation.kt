package com.duoc.corpaliv.navigation

// mapa central de rutas que conecta todas las pantallas y el menu lateral
// decide que pantalla mostrar segun las ordenes que recibe de los viewmodels

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.duoc.corpaliv.ui.components.AppDrawerContent
import com.duoc.corpaliv.ui.screens.AcercaDeScreen
import com.duoc.corpaliv.ui.screens.CatalogScreen
import com.duoc.corpaliv.ui.screens.ContactScreen
import com.duoc.corpaliv.ui.screens.DetalleScreen
import com.duoc.corpaliv.ui.screens.EstadoScreen
import com.duoc.corpaliv.ui.screens.HomeScreen
import com.duoc.corpaliv.ui.screens.LoginScreen
import com.duoc.corpaliv.ui.screens.PerfilScreen
import com.duoc.corpaliv.ui.screens.ResumenScreen
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.MainViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

// funcion principal que dibuja el menu lateral y el contenedor de pantallas
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = viewModel(),
    estadoViewModel: EstadoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route ?: Screen.Login.route

    // si el menu lateral esta abierto el boton atras del telefono solo lo cierra
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // escucha las ordenes de navegacion en segundo plano y cambia de pantalla
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { evento ->
            when (evento) {
                // abre la pantalla solicitada
                is NavigationEvent.NavigateTo -> {
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
                // navegacion de barra inferior que vuelve a inicio sin acumular pantallas
                is NavigationEvent.NavigateBottom -> {
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
                // entra a inicio y borra el login del historial para no volver atras
                is NavigationEvent.LoginExitoso -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                // cierra la sesion, limpia el historial completo y regresa al login
                is NavigationEvent.CerrarSesion -> {
                    usuarioViewModel.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                // vuelve a la pantalla inmediatamente anterior
                is NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val abrirMenu: () -> Unit = {
        scope.launch { drawerState.open() }
    }

    val cerrarMenuYNavegar: (Screen) -> Unit = { destino ->
        scope.launch {
            drawerState.close()
            mainViewModel.navegarA(destino)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = rutaActual != Screen.Login.route,
        drawerContent = {
            AppDrawerContent(
                alCerrarMenu = { scope.launch { drawerState.close() } },
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
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {
            // pantalla 1: inicio de sesion
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = usuarioViewModel,
                    alIngresar = { mainViewModel.ingresarAlHome() }
                )
            }

            // pantalla 2: inicio con saludo y programas destacados
            composable(Screen.Home.route) {
                HomeScreen(
                    usuarioViewModel = usuarioViewModel,
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alSeleccionarPrograma = { programa ->
                        usuarioViewModel.seleccionarPrograma(programa)
                        mainViewModel.navegarA(Screen.Detalle)
                    },
                    alSeleccionarColaboracion = { tipo ->
                        usuarioViewModel.preseleccionarColaboracion(tipo)
                        mainViewModel.navegarA(Screen.Contacto)
                    },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 3: catalogo con los programas disponibles
            composable(Screen.Catalogo.route) {
                CatalogScreen(
                    estadoViewModel = estadoViewModel,
                    soloFavoritos = false,
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alSeleccionarPrograma = { programa ->
                        usuarioViewModel.seleccionarPrograma(programa)
                        mainViewModel.navegarA(Screen.Detalle)
                    },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 4: favoritos guardados por el usuario
            composable(Screen.Favoritos.route) {
                CatalogScreen(
                    estadoViewModel = estadoViewModel,
                    soloFavoritos = true,
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alSeleccionarPrograma = { programa ->
                        usuarioViewModel.seleccionarPrograma(programa)
                        mainViewModel.navegarA(Screen.Detalle)
                    },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 5: detalle completo de un programa seleccionado
            composable(Screen.Detalle.route) {
                DetalleScreen(
                    usuarioViewModel = usuarioViewModel,
                    estadoViewModel = estadoViewModel,
                    alVolver = { mainViewModel.volverAtras() },
                    alQuieroColaborar = { mainViewModel.navegarA(Screen.Contacto) }
                )
            }

            // pantalla 6: formulario para enviar una propuesta de ayuda
            composable(Screen.Contacto.route) {
                ContactScreen(
                    viewModel = usuarioViewModel,
                    alIrAResumen = { mainViewModel.navegarA(Screen.Resumen) },
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 7: comprobante con el folio de la solicitud enviada
            composable(Screen.Resumen.route) {
                ResumenScreen(
                    viewModel = usuarioViewModel,
                    alIrAInicio = { mainViewModel.navegarA(Screen.Home) },
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alAbrirMenu = abrirMenu,
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 8: informacion de la fundacion corpaliv
            composable(Screen.AcercaDe.route) {
                AcercaDeScreen(
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 9: perfil del usuario con su historial de solicitudes
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    usuarioViewModel = usuarioViewModel,
                    estadoViewModel = estadoViewModel,
                    alAbrirMenu = abrirMenu,
                    alVerSolicitud = { mainViewModel.navegarA(Screen.Resumen) },
                    alIrAFavoritos = { mainViewModel.navegarA(Screen.Favoritos) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            // pantalla 10: configuracion para activar alertas y cambiar tema visual
            composable(Screen.Estado.route) {
                EstadoScreen(
                    alVolver = { mainViewModel.navegarA(Screen.Home) },
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) },
                    viewModel = estadoViewModel
                )
            }
        }
    }
}
