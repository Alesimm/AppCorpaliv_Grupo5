package com.duoc.corpaliv.viewmodel

// gestiona la logica de negocio de usuarios, login, formulario de contacto y sqlite room
// se conecta con validadores, estadodatastore, solicituddao y las pantallas de la interfaz

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
import com.duoc.corpaliv.validation.validarClave
import com.duoc.corpaliv.validation.validarColaboracion
import com.duoc.corpaliv.validation.validarCorreo
import com.duoc.corpaliv.validation.validarCredencialesLogin
import com.duoc.corpaliv.validation.validarMensaje
import com.duoc.corpaliv.validation.validarNombre
import com.duoc.corpaliv.validation.validarRut
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
        // recupera el correo recordado desde datastore si el usuario activo la opcion
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

    // actualiza el correo en el login y limpia el mensaje de error
    fun onCorreoLoginChange(valor: String) {
        _estado.update { it.copy(correoLogin = valor, errorLoginCorreo = null) }
    }

    // actualiza la clave en el login y limpia el mensaje de error
    fun onClaveLoginChange(valor: String) {
        _estado.update { it.copy(claveLogin = valor, errorLoginClave = null) }
    }

    // actualiza la casilla recordarme y limpia datastore si se desmarca
    fun onRecordarCorreoChange(recordar: Boolean) {
        _estado.update { it.copy(recordarCorreo = recordar) }
        if (!recordar) {
            viewModelScope.launch {
                estadoDataStore.guardarCorreoRecordado("")
            }
        }
    }

    // valida credenciales llamando a funciones puras y almacena en datastore si recordarme esta activo
    fun validarLogin(): Boolean {
        val actual = _estado.value
        val errorCorreo = validarCorreo(actual.correoLogin)
        val errorClave = validarClave(actual.claveLogin)

        if (errorCorreo != null || errorClave != null) {
            _estado.update {
                it.copy(
                    errorLoginCorreo = errorCorreo,
                    errorLoginClave = errorClave
                )
            }
            return false
        }

        // verifica contra el usuario de prueba unico del caso
        val errorCredenciales = validarCredencialesLogin(actual.correoLogin, actual.claveLogin)
        if (errorCredenciales != null) {
            _estado.update {
                it.copy(
                    errorLoginCorreo = null,
                    errorLoginClave = errorCredenciales
                )
            }
            return false
        }

        val nombreExtraido = actual.correoLogin.substringBefore("@")
            .replaceFirstChar { c -> c.uppercase() }

        _estado.update {
            it.copy(
                nombreUsuarioActivo = nombreExtraido.ifBlank { "Colaborador" },
                correoUsuarioActivo = actual.correoLogin.trim(),
                claveLogin = "",
                errorLoginCorreo = null,
                errorLoginClave = null
            )
        }

        viewModelScope.launch {
            if (actual.recordarCorreo) {
                estadoDataStore.guardarCorreoRecordado(actual.correoLogin.trim())
            } else {
                estadoDataStore.guardarCorreoRecordado("")
            }
        }

        return true
    }

    // fija el programa seleccionado para ver su detalle o preseleccionarlo en el formulario
    fun seleccionarPrograma(programa: ProgramaSocial) {
        _programaSeleccionado.value = programa
        _estado.update { it.copy(programa = programa.titulo) }
    }

    // preselecciona la modalidad de colaboracion desde la pantalla de inicio
    fun preseleccionarColaboracion(tipo: String) {
        _estado.update {
            it.copy(
                colaboracion = tipo,
                errores = it.errores.copy(colaboracion = null)
            )
        }
    }

    // almacena la solicitud seleccionada en el perfil para visualizar su comprobante
    fun verDetalleSolicitud(solicitud: SolicitudEntity) {
        _solicitudSeleccionada.value = solicitud
    }

    // actualiza el nombre del colaborador y limpia su mensaje de error
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // actualiza el correo del formulario de contacto y limpia su mensaje de error
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // actualiza el tipo de colaboracion y limpia los errores vinculados
    fun onColaboracionChange(valor: String) {
        _estado.update {
            it.copy(
                colaboracion = valor,
                errores = it.errores.copy(
                    colaboracion = null,
                    rutComercial = if (valor != "Alianza empresarial") null else it.errores.rutComercial
                )
            )
        }
    }

    // actualiza el programa social elegido en el formulario
    fun onProgramaFormChange(valor: String) {
        _estado.update { it.copy(programa = valor) }
    }

    // actualiza el rut comercial de empresa y limpia su mensaje de error
    fun onRutComercialChange(valor: String) {
        _estado.update { it.copy(rutComercial = valor, errores = it.errores.copy(rutComercial = null)) }
    }

    // actualiza el mensaje opcional de contacto y limpia su mensaje de error
    fun onMensajeChange(valor: String) {
        _estado.update { it.copy(mensaje = valor, errores = it.errores.copy(mensaje = null)) }
    }

    // valida los campos del formulario de contacto mediante funciones puras desacopladas
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val pideRut = actual.colaboracion == "Alianza empresarial"
        val errorRut = if (pideRut) validarRut(actual.rutComercial) else null

        val errores = UsuarioErrores(
            nombre = validarNombre(actual.nombre),
            correo = validarCorreo(actual.correo),
            colaboracion = validarColaboracion(actual.colaboracion),
            rutComercial = errorRut,
            mensaje = validarMensaje(actual.mensaje)
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.colaboracion,
            errores.rutComercial,
            errores.mensaje
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    // registra una nueva solicitud en room asignando estado segun conexion a internet
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

    // actualiza a estado recibida las solicitudes guardadas sin conexion previa
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

    // remueve una solicitud almacenada en la base de datos sqlite
    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            solicitudDao.eliminarSolicitud(solicitud)
            if (_solicitudSeleccionada.value?.id == solicitud.id) {
                _solicitudSeleccionada.value = null
            }
        }
    }
}