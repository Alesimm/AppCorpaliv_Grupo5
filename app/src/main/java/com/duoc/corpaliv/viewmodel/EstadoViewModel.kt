package com.duoc.corpaliv.viewmodel

// maneja los datos de configuracion como el modo oscuro, alertas y favoritos
// se comunica con datastore para que los cambios queden guardados en el telefono

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.EstadoDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// intermediario que conecta las pantallas de ajustes y favoritos con datastore
class EstadoViewModel(application: Application) : AndroidViewModel(application) {

    private val estadoDataStore = EstadoDataStore(application)

    // avisa a la pantalla si las alertas del modo colaborador estan prendidas
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo

    // controla que el mensaje verde de guardado aparezca por 2 segundos
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje

    // lista reactiva con los codigos de los programas marcados como favoritos
    val favoritos: StateFlow<Set<String>> = estadoDataStore.obtenerFavoritos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("taller_oficios"))

    // avisa a toda la app si debe mostrar el fondo oscuro o el fondo claro
    val esTemaOscuro: StateFlow<Boolean> = estadoDataStore.obtenerTemaOscuro()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // cambia de tema oscuro a claro o viceversa y lo guarda en el telefono
    fun alternarTema() {
        viewModelScope.launch {
            estadoDataStore.guardarTemaOscuro(!esTemaOscuro.value)
        }
    }

    init {
        cargarEstado()
    }

    // lee las preferencias guardadas apenas se entra a la pantalla
    fun cargarEstado() {
        viewModelScope.launch {
            _activo.value = estadoDataStore.obtenerEstado().first() ?: false
        }
    }

    // prende o apaga las alertas y muestra el aviso verde de confirmacion
    fun alternarEstado() {
        viewModelScope.launch {
            val nuevoValor = !(_activo.value ?: false)
            estadoDataStore.guardarEstado(nuevoValor)
            _activo.value = nuevoValor
            _mostrarMensaje.value = true
            delay(2000)
            _mostrarMensaje.value = false
        }
    }

    // guarda o saca un programa de favoritos al tocar el icono de corazon
    fun alternarFavorito(idPrograma: String) {
        viewModelScope.launch {
            estadoDataStore.alternarFavorito(idPrograma)
        }
    }
}
