package com.duoc.corpaliv.navigation

// enrutador central con navegacion por eventos, control de back stack y menu lateral
// se conecta con mainviewmodel, usuarioviewmodel, estadodatastore, screen y las 9 pantallas

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

// gestiona la navegacion global de la aplicacion y el ciclo de vida del back stack
@Composable
fun AppNavigation(mainViewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val estadoViewModel: EstadoViewModel = viewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route ?: Screen.Login.route

    // cierra el menu lateral con el boton atras en lugar de salir de la aplicacion
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // recolecta ordenes de navegacion desacopladas y gestiona popupto y guardado de estado
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { evento ->
            when (evento) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(evento.screen.route) {
                        launchSingleTop = true
                    }
                }
                // popupto con savestate para preservar el scroll y estado de cada pestana inferior
                is NavigationEvent.NavigateBottom -> {
                    navController.navigate(evento.screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                // popupto inclusive para remover el login del historial al entrar al home
                is NavigationEvent.LoginExitoso -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                // vacia el back stack por completo y regresa a login reiniciando sesion
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
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = usuarioViewModel,
                    alIngresar = { mainViewModel.ingresarAlHome() }
                )
            }

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

            composable(Screen.Detalle.route) {
                DetalleScreen(
                    usuarioViewModel = usuarioViewModel,
                    estadoViewModel = estadoViewModel,
                    alVolver = { mainViewModel.volverAtras() },
                    alQuieroColaborar = { mainViewModel.navegarA(Screen.Contacto) }
                )
            }

            composable(Screen.Contacto.route) {
                ContactScreen(
                    viewModel = usuarioViewModel,
                    alIrAResumen = { mainViewModel.navegarA(Screen.Resumen) },
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            composable(Screen.Resumen.route) {
                ResumenScreen(
                    viewModel = usuarioViewModel,
                    alIrAInicio = { mainViewModel.navegarA(Screen.Home) },
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alAbrirMenu = abrirMenu,
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

            composable(Screen.AcercaDe.route) {
                AcercaDeScreen(
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarBottom(destino) }
                )
            }

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
