package com.duoc.corpaliv.navigation

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

// enrutador central que conecta las 9 pantallas del mockup con el menu lateral desplegable
@Composable
fun AppNavigation(mainViewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val estadoViewModel: EstadoViewModel = viewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route ?: Screen.Login.route

    // recolecta los eventos de navegacion emitidos por el mainviewmodel
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { evento ->
            when (evento) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(evento.screen.route) {
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
                alCerrarSesion = { cerrarMenuYNavegar(Screen.Login) }
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
                    alIngresar = { mainViewModel.navegarA(Screen.Home) }
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
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
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
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
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
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
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
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
                )
            }

            composable(Screen.Resumen.route) {
                ResumenScreen(
                    viewModel = usuarioViewModel,
                    alIrAInicio = { mainViewModel.navegarA(Screen.Home) },
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alAbrirMenu = abrirMenu,
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
                )
            }

            composable(Screen.AcercaDe.route) {
                AcercaDeScreen(
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
                )
            }

            composable(Screen.Perfil.route) {
                PerfilScreen(
                    usuarioViewModel = usuarioViewModel,
                    estadoViewModel = estadoViewModel,
                    alAbrirMenu = abrirMenu,
                    alVerSolicitud = { mainViewModel.navegarA(Screen.Resumen) },
                    alIrAFavoritos = { mainViewModel.navegarA(Screen.Favoritos) },
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) }
                )
            }

            composable(Screen.Estado.route) {
                EstadoScreen(
                    alVolver = { mainViewModel.navegarA(Screen.Home) },
                    alAbrirMenu = abrirMenu,
                    alIrAPerfil = { mainViewModel.navegarA(Screen.Perfil) },
                    alNavegarBottom = { destino -> mainViewModel.navegarA(destino) },
                    viewModel = estadoViewModel
                )
            }
        }
    }
}