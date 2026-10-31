package com.duoc.corpaliv.viewmodel

// controlador central para pedir cambios de pantalla desde cualquier lugar de la app
// envia ordenes de navegacion limpias para no enredar la interfaz con codigo de rutas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.navigation.NavigationEvent
import com.duoc.corpaliv.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// intermediario que recibe solicitudes de cambio de pantalla y las transmite
class MainViewModel : ViewModel() {

    // canal en vivo que lleva los avisos de cambio de pantalla a la interfaz
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // envia la orden de abrir una pantalla determinada
    fun navegarA(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(screen))
        }
    }

    // envia la orden para los botones de la barra inferior
    fun navegarBottom(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateBottom(screen))
        }
    }

    // envia la orden al entrar con exito para ir a inicio y sacar el login
    fun ingresarAlHome() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.LoginExitoso)
        }
    }

    // envia la orden de cerrar sesion y regresar al login desde cero
    fun cerrarSesion() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.CerrarSesion)
        }
    }

    // envia la orden de retroceder a la pantalla anterior
    fun volverAtras() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}
