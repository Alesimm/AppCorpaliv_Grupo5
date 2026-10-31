package com.duoc.corpaliv.navigation

// tipos de ordenes que piden cambiar de pantalla o retroceder
// permite que los viewmodels pidan navegar sin tocar las vistas de compose

// ordenes posibles de navegacion como ir a pantalla, volver o salir
sealed class NavigationEvent {
    // orden normal para ir hacia una pantalla nueva
    data class NavigateTo(val screen: Screen) : NavigationEvent()

    // orden de la barra inferior para cambiar de pestana sin trabarse
    data class NavigateBottom(val screen: Screen) : NavigationEvent()

    // orden al iniciar sesion para ir a inicio y sacar el login del historial
    object LoginExitoso : NavigationEvent()

    // orden para cerrar sesion, borrar todo el historial y volver a login
    object CerrarSesion : NavigationEvent()

    // orden para volver hacia atras a la pantalla anterior
    object PopBackStack : NavigationEvent()
}
