package com.duoc.corpaliv.viewmodel

// emite eventos reactivos de navegacion para desacoplar las vistas del navcontroller
// se conecta con navigationevent, screen y appnavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.navigation.NavigationEvent
import com.duoc.corpaliv.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // canal compartido que transporta las ordenes de navegacion hacia la interfaz
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // emite orden de navegacion hacia una pantalla especifica
    fun navegarA(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(screen))
        }
    }

    // emite orden de navegacion para la barra inferior guardando el estado
    fun navegarBottom(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateBottom(screen))
        }
    }

    // emite orden de acceso al home tras autenticarse para remover login del stack
    fun ingresarAlHome() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.LoginExitoso)
        }
    }

    // emite orden de cierre de sesion para vaciar el stack y volver al login
    fun cerrarSesion() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.CerrarSesion)
        }
    }

    // emite orden para volver a la pantalla anterior
    fun volverAtras() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}
