package com.duoc.corpaliv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.AppDatabase
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.model.UsuarioErrores
import com.duoc.corpaliv.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// hereda de androidviewmodel para pasar el contexto a la base de datos room
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    // obtiene el dao de room para ejecutar las consultas sqlite
    private val solicitudDao = AppDatabase.obtenerBaseDatos(application).solicitudDao()

    // estado interno mutable del formulario
    private val _estado = MutableStateFlow(UsuarioUiState())
    // estado expuesto de solo lectura para que la pantalla lo observe
    val estado: StateFlow<UsuarioUiState> = _estado

    // convierte el flujo de sqlite en un stateflow para listar las solicitudes en pantalla
    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao.obtenerSolicitudes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // actualiza el nombre escrito y borra el mensaje de error de ese campo
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // actualiza el correo escrito y borra el mensaje de error de ese campo
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // actualiza la clave escrita y borra el mensaje de error de ese campo
    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    // actualiza la direccion escrita y borra el mensaje de error de ese campo
    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    // actualiza la casilla de aceptacion de terminos
    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    // revisa que todos los campos cumplan las reglas antes de guardar
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    // create: inserta la solicitud en sqlite marcando si habia internet o si quedo pendiente
    fun guardarEnSqlite(hayInternet: Boolean) {
        val actual = _estado.value
        val folioGenerado = "ALBA-${(1000..9999).random()}"
        val estadoInicial = if (hayInternet) {
            "Sincronizado"
        } else {
            "Pendiente (Sin internet)"
        }

        val nuevaSolicitud = SolicitudEntity(
            folio = folioGenerado,
            nombre = actual.nombre,
            correo = actual.correo,
            direccion = actual.direccion,
            estadoEnvio = estadoInicial
        )

        viewModelScope.launch {
            solicitudDao.insertarSolicitud(nuevaSolicitud)
        }
    }

    // update: avanza el estado de una solicitud en sqlite para demostrar edicion crud
    fun cambiarEstadoSolicitud(solicitud: SolicitudEntity) {
        val siguienteEstado = when (solicitud.estadoEnvio) {
            "Pendiente (Sin internet)" -> "Sincronizado"
            "Sincronizado" -> "En revisión CORPALIV"
            else -> "Aprobado"
        }
        viewModelScope.launch {
            solicitudDao.actualizarSolicitud(solicitud.copy(estadoEnvio = siguienteEstado))
        }
    }

    // update masivo: pasa todas las solicitudes pendientes a sincronizadas
    fun sincronizarPendientes() {
        viewModelScope.launch {
            solicitudDao.sincronizarPendientes()
        }
    }

    // delete: elimina una solicitud seleccionada de la tabla sqlite
    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            solicitudDao.eliminarSolicitud(solicitud)
        }
    }
}