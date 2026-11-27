package com.duoc.corpaliv.viewmodel

// cerebro principal de la app que maneja el login, el formulario y la base de datos sqlite
// valida los datos ingresados, guarda solicitudes y avisa a las vistas cuando algo cambia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.AppDatabase
import com.duoc.corpaliv.data.EstadoDataStore
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.data.SolicitudRepository
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.SesionActiva
import com.duoc.corpaliv.model.UsuarioErrores
import com.duoc.corpaliv.model.UsuarioUiState
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.remote.RetrofitClient
import com.duoc.corpaliv.validation.CredencialesPrueba
import com.duoc.corpaliv.validation.validarApellido
import com.duoc.corpaliv.validation.validarClaveLogin
import com.duoc.corpaliv.validation.validarColaboracion
import com.duoc.corpaliv.validation.validarCorreo
import com.duoc.corpaliv.validation.validarCredencialesLogin
import com.duoc.corpaliv.validation.validarMensaje
import com.duoc.corpaliv.validation.validarNombre
import com.duoc.corpaliv.validation.validarRut
import kotlinx.coroutines.delay
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

// estado liviano para mostrar animacion y aviso al recargar solicitudes desde el servidor
data class EstadoRecargaSolicitudes(
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false
)

// viewmodel principal que administra la sesion del usuario y las solicitudes de ayuda
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val solicitudRepository = SolicitudRepository(
        solicitudDao = AppDatabase.obtenerBaseDatos(application).solicitudDao(),
        api = RetrofitClient.solicitudApi
    )
    private val estadoDataStore = EstadoDataStore(application)

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    // flujo liviano solo con el usuario logueado para no recomponer barras ni inicio al escribir en contacto
    private val _sesionActiva = MutableStateFlow(SesionActiva())
    val sesionActiva: StateFlow<SesionActiva> = _sesionActiva

    private val _programaSeleccionado = MutableStateFlow(listaProgramasAlbaLab.first())
    val programaSeleccionado: StateFlow<ProgramaSocial> = _programaSeleccionado

    private val _solicitudSeleccionada = MutableStateFlow<SolicitudEntity?>(null)
    val solicitudSeleccionada: StateFlow<SolicitudEntity?> = _solicitudSeleccionada

    // activa el skeleton breve solo al emitir una nueva solicitud para una transicion mas natural
    private val _cargandoComprobante = MutableStateFlow(false)
    val cargandoComprobante: StateFlow<Boolean> = _cargandoComprobante

    private val _estadoRecarga = MutableStateFlow(EstadoRecargaSolicitudes())
    val estadoRecarga: StateFlow<EstadoRecargaSolicitudes> = _estadoRecarga

    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudRepository.solicitudes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // al iniciar lee el correo recordado e intenta sincronizar solicitudes desde el microservicio
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
            solicitudRepository.refrescarDesdeServidor()
        }
    }

    // actualiza el texto del correo mientras el usuario escribe en el login
    fun onCorreoLoginChange(valor: String) {
        _estado.update { it.copy(correoLogin = valor, errorLoginCorreo = null, errorLoginClave = null) }
    }

    // actualiza el texto de la clave mientras el usuario escribe en el login
    fun onClaveLoginChange(valor: String) {
        _estado.update { it.copy(claveLogin = valor, errorLoginCorreo = null, errorLoginClave = null) }
    }

    // activa o desactiva la casilla recordarme y borra el dato si se desmarca
    fun onRecordarCorreoChange(recordar: Boolean) {
        _estado.update { it.copy(recordarCorreo = recordar) }
        if (!recordar) {
            viewModelScope.launch {
                estadoDataStore.guardarCorreoRecordado("")
            }
        }
    }

    // revisa que el correo y la clave no esten vacios y coincidan sin dar pistas de cual fallo
    fun validarLogin(): Boolean {
        val actual = _estado.value
        val errorCorreo = validarCorreo(actual.correoLogin)
        val errorClave = validarClaveLogin(actual.claveLogin)

        if (errorCorreo != null || errorClave != null) {
            _estado.update {
                it.copy(
                    errorLoginCorreo = errorCorreo,
                    errorLoginClave = errorClave
                )
            }
            return false
        }

        // revisa contra las credenciales del caso sin revelar si fallo el correo o la clave
        val errorCredenciales = validarCredencialesLogin(actual.correoLogin, actual.claveLogin)
        if (errorCredenciales != null) {
            _estado.update {
                it.copy(
                    errorLoginCorreo = "",
                    errorLoginClave = errorCredenciales
                )
            }
            return false
        }

        val nombreExtraido = actual.correoLogin.substringBefore("@")
            .replaceFirstChar { c -> c.uppercase() }
        val nombreFinal = nombreExtraido.ifBlank { "Colaborador" }
        val correoFinal = actual.correoLogin.trim()

        _sesionActiva.value = SesionActiva(nombre = nombreFinal, correo = correoFinal)

        _estado.update {
            it.copy(
                nombreUsuarioActivo = nombreFinal,
                correoUsuarioActivo = correoFinal,
                claveLogin = actual.claveLogin,
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

    // guarda el programa que el usuario toco para mostrarlo en el detalle o en el formulario
    fun seleccionarPrograma(programa: ProgramaSocial) {
        _programaSeleccionado.value = programa
        _estado.update { it.copy(programa = programa.titulo) }
    }

    // preselecciona la forma de ayuda como donacion o voluntariado desde la pantalla de inicio
    fun preseleccionarColaboracion(tipo: String) {
        _estado.update {
            it.copy(
                colaboracion = tipo,
                errores = it.errores.copy(colaboracion = null)
            )
        }
    }

    // guarda la solicitud elegida en el perfil para abrir su comprobante de inmediato
    fun verDetalleSolicitud(solicitud: SolicitudEntity) {
        _cargandoComprobante.value = false
        _solicitudSeleccionada.value = solicitud
    }

    // actualiza el nombre del colaborador y borra el error en rojo si lo corrige
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // actualiza el apellido del colaborador y borra el error en rojo si lo corrige
    fun onApellidoChange(valor: String) {
        _estado.update { it.copy(apellido = valor, errores = it.errores.copy(apellido = null)) }
    }

    // actualiza el correo del formulario y borra el error en rojo si lo corrige
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // actualiza la forma de ayuda elegida y oculta el campo rut si no es alianza
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

    // actualiza el programa elegido en la lista desplegable del formulario
    fun onProgramaFormChange(valor: String) {
        _estado.update { it.copy(programa = valor) }
    }

    // actualiza el rut de empresa ingresado y borra el error en rojo si lo corrige
    fun onRutComercialChange(valor: String) {
        _estado.update { it.copy(rutComercial = valor, errores = it.errores.copy(rutComercial = null)) }
    }

    // actualiza el mensaje opcional que escribe el colaborador
    fun onMensajeChange(valor: String) {
        _estado.update { it.copy(mensaje = valor, errores = it.errores.copy(mensaje = null)) }
    }

    // revisa uno por uno todos los campos del formulario usando las reglas de validacion
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val pideRut = actual.colaboracion == "Alianza empresarial"
        val errorRut = if (pideRut) validarRut(actual.rutComercial) else null

        val errores = UsuarioErrores(
            nombre = validarNombre(actual.nombre),
            apellido = validarApellido(actual.apellido),
            correo = validarCorreo(actual.correo),
            colaboracion = validarColaboracion(actual.colaboracion),
            rutComercial = errorRut,
            mensaje = validarMensaje(actual.mensaje)
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.apellido,
            errores.correo,
            errores.colaboracion,
            errores.rutComercial,
            errores.mensaje
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    // guarda la solicitud en sqlite, abre el skeleton y avisa cuando el comprobante ya esta visible
    fun guardarEnSqlite(
        hayInternet: Boolean,
        onGuardado: (String) -> Unit = {},
        onComprobanteListo: (String) -> Unit = {}
    ) {
        val actual = _estado.value
        val fechaHoy = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val estadoInicial = if (hayInternet) "RECIBIDA" else "PENDIENTE SIN RED"

        val solicitudInicial = SolicitudEntity(
            folio = "",
            fecha = fechaHoy,
            colaboracion = actual.colaboracion,
            programa = actual.programa.ifBlank { listaProgramasAlbaLab.first().titulo },
            nombre = actual.nombre.trim(),
            apellido = actual.apellido.trim(),
            correo = actual.correo,
            rutComercial = if (actual.rutComercial.isBlank()) "No aplica" else actual.rutComercial,
            mensaje = if (actual.mensaje.isBlank()) "Sin mensaje adicional." else actual.mensaje,
            estadoEnvio = estadoInicial
        )

        viewModelScope.launch {
            _cargandoComprobante.value = true
            // intenta enviar al microservicio spring boot y cae a room como respaldo si no hay red
            val solicitudFinal = solicitudRepository.crearSolicitud(solicitudInicial, hayInternet)
            _solicitudSeleccionada.value = solicitudFinal

            // borra lo que habia en los campos del formulario para dejarlo limpio
            limpiarFormularioContacto()

            onGuardado(solicitudFinal.folio)

            // muestra el skeleton del comprobante para una transicion natural antes de revelar el folio
            delay(1350)
            _cargandoComprobante.value = false

            // espera breve para que el ticket animado se dibuje y luego dispara la notificacion
            delay(350)
            onComprobanteListo(solicitudFinal.folio)
        }
    }

    // deja todos los campos del formulario en blanco para una nueva solicitud
    fun limpiarFormularioContacto() {
        _estado.update {
            it.copy(
                nombre = "",
                apellido = "",
                correo = "",
                colaboracion = "",
                programa = "",
                rutComercial = "",
                mensaje = "",
                errores = UsuarioErrores()
            )
        }
    }

    // cierra la sesion del usuario pero conserva el correo si marco recordarme
    fun cerrarSesion() {
        val recordar = _estado.value.recordarCorreo
        val correo = if (recordar) _estado.value.correoLogin else CredencialesPrueba.CORREO
        _estado.value = UsuarioUiState(
            correoLogin = correo,
            claveLogin = CredencialesPrueba.CLAVE,
            recordarCorreo = recordar
        )
        _sesionActiva.value = SesionActiva()
        _solicitudSeleccionada.value = null
    }

    // reenvia las solicitudes guardadas sin red al microservicio y actualiza el historial
    fun sincronizarPendientes() {
        viewModelScope.launch {
            val enviadas = solicitudRepository.sincronizarPendientes()
            val ok = solicitudRepository.refrescarDesdeServidor()
            if (enviadas > 0) {
                _solicitudSeleccionada.value?.let { actual ->
                    if (actual.estadoEnvio.startsWith("PENDIENTE")) {
                        _solicitudSeleccionada.value = actual.copy(estadoEnvio = "RECIBIDA")
                    }
                }
                _estadoRecarga.value = EstadoRecargaSolicitudes(
                    cargando = false,
                    mensaje = "Pendientes sincronizadas con el servidor",
                    esError = false
                )
                delay(2500)
                _estadoRecarga.update { it.copy(mensaje = null) }
            } else if (!ok) {
                _estadoRecarga.value = EstadoRecargaSolicitudes(
                    cargando = false,
                    mensaje = "Sin conexión con el servidor",
                    esError = true
                )
                delay(2500)
                _estadoRecarga.update { it.copy(mensaje = null) }
            }
        }
    }

    // recarga desde el microservicio, sube pendientes automaticamente y muestra aviso si corresponde
    fun recargarSolicitudesServidor(silencioso: Boolean = false) {
        if (_estadoRecarga.value.cargando) return
        viewModelScope.launch {
            if (!silencioso) {
                _estadoRecarga.value = EstadoRecargaSolicitudes(cargando = true)
                delay(350)
            }
            val actualizado = solicitudRepository.refrescarDesdeServidor()
            if (actualizado) {
                _solicitudSeleccionada.value?.let { actual ->
                    if (actual.estadoEnvio.startsWith("PENDIENTE")) {
                        _solicitudSeleccionada.value = actual.copy(estadoEnvio = "RECIBIDA")
                    }
                }
            }
            if (!silencioso) {
                _estadoRecarga.value = if (actualizado) {
                    EstadoRecargaSolicitudes(
                        cargando = false,
                        mensaje = "Solicitudes actualizadas",
                        esError = false
                    )
                } else {
                    EstadoRecargaSolicitudes(
                        cargando = false,
                        mensaje = "Sin conexión con el servidor",
                        esError = true
                    )
                }
                delay(2500)
                _estadoRecarga.update { it.copy(mensaje = null) }
            }
        }
    }

    // borra una solicitud en el microservicio y en room cuando el usuario confirma
    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            solicitudRepository.eliminarSolicitud(solicitud)
            if (_solicitudSeleccionada.value?.id == solicitud.id) {
                _solicitudSeleccionada.value = null
            }
        }
    }

    // actualiza los datos editados en room y sincroniza por put con el microservicio
    fun actualizarSolicitudExistente(
        solicitud: SolicitudEntity,
        nuevaColaboracion: String,
        nuevoRut: String,
        nuevoMensaje: String
    ) {
        val rutFinal = if (nuevaColaboracion == "Alianza empresarial") {
            nuevoRut.trim()
        } else {
            "No aplica"
        }
        val mensajeFinal = nuevoMensaje.trim().ifBlank { "Sin mensaje adicional." }

        val solicitudActualizada = solicitud.copy(
            colaboracion = nuevaColaboracion,
            rutComercial = rutFinal,
            mensaje = mensajeFinal
        )

        viewModelScope.launch {
            val actualizada = solicitudRepository.actualizarSolicitud(solicitudActualizada)
            _solicitudSeleccionada.value = actualizada
        }
    }
}
