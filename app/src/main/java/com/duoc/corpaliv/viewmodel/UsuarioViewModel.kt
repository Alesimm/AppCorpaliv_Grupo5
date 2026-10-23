package com.duoc.corpaliv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.AppDatabase
import com.duoc.corpaliv.data.EstadoDataStore
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.UsuarioErrores
import com.duoc.corpaliv.model.UsuarioUiState
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Centraliza la lógica de negocio del login, el formulario de contacto y la base de datos Room.
// Se conecta con EstadoDataStore, SolicitudDao y las pantallas de la interfaz.
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val solicitudDao = AppDatabase.obtenerBaseDatos(application).solicitudDao()
    private val estadoDataStore = EstadoDataStore(application)

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    private val _programaSeleccionado = MutableStateFlow(listaProgramasAlbaLab.first())
    val programaSeleccionado: StateFlow<ProgramaSocial> = _programaSeleccionado

    private val _solicitudSeleccionada = MutableStateFlow<SolicitudEntity?>(null)
    val solicitudSeleccionada: StateFlow<SolicitudEntity?> = _solicitudSeleccionada

    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao.obtenerSolicitudes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Al iniciar el ViewModel, recupera el correo guardado en DataStore si el usuario marcó Recordarme.
        viewModelScope.launch {
            val correoRecordado = estadoDataStore.obtenerCorreoRecordado().first()
            if (correoRecordado.isNotBlank()) {
                _estado.update {
                    it.copy(
                        correoLogin = correoRecordado,
                        recordarCorreo = true
                    )
                }
            }
        }
    }

    // Actualiza el correo escrito en el login y limpia su mensaje de error.
    fun onCorreoLoginChange(valor: String) {
        _estado.update { it.copy(correoLogin = valor, errorLoginCorreo = null) }
    }

    // Actualiza la contraseña escrita en el login y limpia su mensaje de error.
    fun onClaveLoginChange(valor: String) {
        _estado.update { it.copy(claveLogin = valor, errorLoginClave = null) }
    }

    // Cambia el estado del checkbox Recordarme y borra el correo guardado si se desmarca.
    fun onRecordarCorreoChange(recordar: Boolean) {
        _estado.update { it.copy(recordarCorreo = recordar) }
        if (!recordar) {
            viewModelScope.launch {
                estadoDataStore.guardarCorreoRecordado("")
            }
        }
    }

    // Valida el acceso del usuario y persiste únicamente el correo en DataStore si marcó Recordarme.
    fun validarLogin(): Boolean {
        val actual = _estado.value
        val errorCorreo = if (!actual.correoLogin.contains("@")) "Ingresa un correo válido" else null
        val errorClave = if (actual.claveLogin.length < 8) "La contraseña debe tener mínimo 8 caracteres" else null

        _estado.update {
            it.copy(
                errorLoginCorreo = errorCorreo,
                errorLoginClave = errorClave
            )
        }

        val valido = errorCorreo == null && errorClave == null
        if (valido) {
            val nombreExtraido = actual.correoLogin.substringBefore("@")
                .replaceFirstChar { c -> c.uppercase() }
            _estado.update {
                it.copy(
                    nombreUsuarioActivo = nombreExtraido.ifBlank { "Colaborador" },
                    correoUsuarioActivo = actual.correoLogin,
                    claveLogin = ""
                )
            }
            viewModelScope.launch {
                if (actual.recordarCorreo) {
                    estadoDataStore.guardarCorreoRecordado(actual.correoLogin)
                } else {
                    estadoDataStore.guardarCorreoRecordado("")
                }
            }
        }
        return valido
    }

    // Selecciona un programa social para ver su detalle o preseleccionarlo en el formulario.
    fun seleccionarPrograma(programa: ProgramaSocial) {
        _programaSeleccionado.value = programa
        _estado.update { it.copy(programa = programa.titulo) }
    }

    // Preselecciona el tipo de colaboración elegido desde la pantalla de inicio.
    fun preseleccionarColaboracion(tipo: String) {
        _estado.update {
            it.copy(
                colaboracion = tipo,
                errores = it.errores.copy(colaboracion = null)
            )
        }
    }

    // Guarda la solicitud tocada en el perfil para mostrar su comprobante en ResumenScreen.
    fun verDetalleSolicitud(solicitud: SolicitudEntity) {
        _solicitudSeleccionada.value = solicitud
    }

    // Actualiza el nombre en el formulario de contacto y limpia su error.
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // Actualiza el correo en el formulario de contacto y limpia su error.
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // Actualiza el tipo de colaboración seleccionado y limpia su error.
    fun onColaboracionChange(valor: String) {
        _estado.update { it.copy(colaboracion = valor, errores = it.errores.copy(colaboracion = null)) }
    }

    // Actualiza el programa seleccionado en el formulario de contacto.
    fun onProgramaFormChange(valor: String) {
        _estado.update { it.copy(programa = valor) }
    }

    // Actualiza el RUT comercial de la empresa y limpia su error.
    fun onRutComercialChange(valor: String) {
        _estado.update { it.copy(rutComercial = valor, errores = it.errores.copy(rutComercial = null)) }
    }

    // Actualiza el mensaje opcional de la solicitud.
    fun onMensajeChange(valor: String) {
        _estado.update { it.copy(mensaje = valor) }
    }

    // Revisa los campos obligatorios del formulario de contacto antes de guardar en SQLite.
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val pideRut = actual.colaboracion == "Alianza empresarial"
        val errores = UsuarioErrores(
            nombre = if (actual.nombre.isBlank()) "Ingresa tu nombre y apellido" else null,
            correo = if (!actual.correo.contains("@")) "Ingresa un correo válido" else null,
            colaboracion = if (actual.colaboracion.isBlank()) "Selecciona cómo quieres ayudar" else null,
            rutComercial = if (pideRut && actual.rutComercial.isBlank()) "Ingresa el RUT comercial" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.colaboracion,
            errores.rutComercial
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    // Inserta una nueva solicitud en la tabla SQLite de Room según el estado de la conexión.
    fun guardarEnSqlite(hayInternet: Boolean) {
        val actual = _estado.value
        val numeroFolio = (1000..9999).random()
        val folioGenerado = "AL-2026-$numeroFolio"
        val fechaHoy = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val estadoInicial = if (hayInternet) "RECIBIDA" else "PENDIENTE SIN RED"

        val nuevaSolicitud = SolicitudEntity(
            folio = folioGenerado,
            fecha = fechaHoy,
            colaboracion = actual.colaboracion,
            programa = actual.programa.ifBlank { listaProgramasAlbaLab.first().titulo },
            nombre = actual.nombre,
            correo = actual.correo,
            rutComercial = if (actual.rutComercial.isBlank()) "No aplica" else actual.rutComercial,
            mensaje = if (actual.mensaje.isBlank()) "Sin mensaje adicional." else actual.mensaje,
            estadoEnvio = estadoInicial
        )

        _solicitudSeleccionada.value = nuevaSolicitud

        viewModelScope.launch {
            solicitudDao.insertarSolicitud(nuevaSolicitud)
        }
    }

    // Pasa las solicitudes pendientes a estado RECIBIDA cuando se restablece la red.
    fun sincronizarPendientes() {
        viewModelScope.launch {
            solicitudDao.sincronizarPendientes()
            _solicitudSeleccionada.value?.let { actual ->
                if (actual.estadoEnvio.startsWith("PENDIENTE")) {
                    _solicitudSeleccionada.value = actual.copy(estadoEnvio = "RECIBIDA")
                }
            }
        }
    }

    // Elimina una solicitud de la base de datos local SQLite Room.
    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            solicitudDao.eliminarSolicitud(solicitud)
            if (_solicitudSeleccionada.value?.id == solicitud.id) {
                _solicitudSeleccionada.value = null
            }
        }
    }
}