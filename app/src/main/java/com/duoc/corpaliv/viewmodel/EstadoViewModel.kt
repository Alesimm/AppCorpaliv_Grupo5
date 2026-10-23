package com.duoc.corpaliv.viewmodel

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

// administra las preferencias locales y los programas favoritos con datastore
class EstadoViewModel(application: Application) : AndroidViewModel(application) {

    private val estadoDataStore = EstadoDataStore(application)

    // estado que indica si el modo colaborador esta activo o cargando
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo

    // controla la aparicion animada del mensaje de confirmacion
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje

    // flujo reactivo con los ids de los programas marcados como favoritos
    val favoritos: StateFlow<Set<String>> = estadoDataStore.obtenerFavoritos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("taller_oficios"))

    init {
        cargarEstado()
    }

    fun cargarEstado() {
        viewModelScope.launch {
            delay(1500)
            _activo.value = estadoDataStore.obtenerEstado().first() ?: false
        }
    }

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

    // marca o desmarca un programa social como favorito en datastore
    fun alternarFavorito(idPrograma: String) {
        viewModelScope.launch {
            estadoDataStore.alternarFavorito(idPrograma)
        }
    }
}