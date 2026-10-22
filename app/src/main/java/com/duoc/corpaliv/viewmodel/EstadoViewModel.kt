package com.duoc.corpaliv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.EstadoDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// extiende de androidviewmodel para poder usar el contexto de la app
class EstadoViewModel(application: Application) : AndroidViewModel(application) {

    // instancia datastore con el contexto de la aplicacion
    private val estadoDataStore = EstadoDataStore(application)

    // estado que representa si el modo esta activado o cargando
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo

    // estado para mostrar u ocultar el mensaje animado
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje

    init {
        // al iniciar el viewmodel carga el estado desde datastore
        cargarEstado()
    }

    fun cargarEstado() {
        viewModelScope.launch {
            // simula demora inicial para mostrar el indicador de carga
            delay(1500)
            _activo.value = estadoDataStore.obtenerEstado().first() ?: false
        }
    }

    fun alternarEstado() {
        viewModelScope.launch {
            // invierte el valor actual del estado
            val nuevoValor = !(_activo.value ?: false)

            // guarda el nuevo valor en datastore
            estadoDataStore.guardarEstado(nuevoValor)

            // actualiza el flujo observable
            _activo.value = nuevoValor

            // muestra el mensaje visual animado y lo oculta tras 2 segundos
            _mostrarMensaje.value = true
            delay(2000)
            _mostrarMensaje.value = false
        }
    }
}