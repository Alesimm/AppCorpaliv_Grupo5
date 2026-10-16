package com.duoc.corpaliv.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.corpaliv.ui.screens.CatalogScreen
import com.duoc.corpaliv.ui.screens.ContactScreen
import com.duoc.corpaliv.ui.screens.HomeScreen
import com.duoc.corpaliv.viewmodel.MainViewModel

@Composable
fun AppNavigation(mainViewModel: MainViewModel = viewModel()) {
    // controlador que maneja el historial de pantallas
    val navController = rememberNavController()

    // escucha los eventos que manda el viewmodel y ejecuta el cambio de pantalla
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

    // contenedor principal de rutas que inicia en home
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                alIrACatalogo = { mainViewModel.navegarA(Screen.Catalogo) },
                alIrAContacto = { mainViewModel.navegarA(Screen.Contacto) }
            )
        }
        composable(Screen.Catalogo.route) {
            CatalogScreen(
                alVolver = { mainViewModel.volverAtras() },
                alIrAContacto = { mainViewModel.navegarA(Screen.Contacto) }
            )
        }
        composable(Screen.Contacto.route) {
            ContactScreen(
                alVolver = { mainViewModel.volverAtras() }
            )
        }
    }
}