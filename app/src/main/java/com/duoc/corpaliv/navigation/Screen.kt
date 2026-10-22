package com.duoc.corpaliv.navigation

// clase sellada con las rutas de la aplicacion
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object Catalogo : Screen("catalogo_screen")
    object Contacto : Screen("contacto_screen")
    object Resumen : Screen("resumen_screen")
    object Estado : Screen("estado_screen")
    object Favoritos : Screen("favoritos_screen")
    object AcercaDe : Screen("acerca_de_screen")
}