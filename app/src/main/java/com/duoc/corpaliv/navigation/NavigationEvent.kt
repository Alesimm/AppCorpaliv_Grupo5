package com.duoc.corpaliv.navigation

// eventos sellados para coordinar la navegacion y el back stack entre vistas
// se conecta con mainviewmodel, screen y appnavigation

sealed class NavigationEvent {
    // navegacion estandar hacia una pantalla
    data class NavigateTo(val screen: Screen) : NavigationEvent()

    // navegacion de barra inferior con preservacion de estado por pestana
    data class NavigateBottom(val screen: Screen) : NavigationEvent()

    // navegacion desde login al home eliminando el login del back stack
    object LoginExitoso : NavigationEvent()

    // cierre de sesion con limpieza completa del back stack para volver a login
    object CerrarSesion : NavigationEvent()

    // retroceso simple a la pantalla anterior
    object PopBackStack : NavigationEvent()
}
