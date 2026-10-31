package com.duoc.corpaliv.navigation

// lista de nombres oficiales para cada pantalla de la aplicacion
// evita escribir texto a mano y equivocarse en los nombres de las rutas

// clase sellada con las rutas de las 10 pantallas de la app
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object Catalogo : Screen("catalogo_screen")
    object Detalle : Screen("detalle_screen")
    object Contacto : Screen("contacto_screen")
    object Resumen : Screen("resumen_screen")
    object Estado : Screen("estado_screen")
    object Favoritos : Screen("favoritos_screen")
    object AcercaDe : Screen("acerca_de_screen")
    object Perfil : Screen("perfil_screen")
}
