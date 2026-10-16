package com.duoc.corpaliv.navigation

// eventos para indicar a que pantalla ir o volver atras
sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
    object PopBackStack : NavigationEvent()
}