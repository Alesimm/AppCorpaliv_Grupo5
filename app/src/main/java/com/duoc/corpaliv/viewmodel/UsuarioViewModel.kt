package com.duoc.corpaliv.viewmodel

// cerebro principal de la app que maneja el login, el formulario y la base de datos sqlite
// valida los datos ingresados, guarda solicitudes y avisa a las vistas cuando algo cambia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.AppDatabase
import com.duoc.corpaliv.data.EstadoDataStore
import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.model.ProgramaSocial
import com.duoc.corpaliv.model.SesionActiva
import com.duoc.corpaliv.model.UsuarioErrores
import com.duoc.corpaliv.model.UsuarioUiState
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import com.duoc.corpaliv.validation.CredencialesPrueba
import com.duoc.corpaliv.validation.validarApellido
import com.duoc.corpaliv.validation.validarClaveLogin
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

// viewmodel principal que administra la sesion del usuario y las solicitudes de ayuda
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val solicitudDao = AppDatabase.obtenerBaseDatos(application).solicitudDao()
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

    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao.obtenerSolicitudes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // al iniciar la app lee si habia un correo guardado con la opcion recordarme
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

    // guarda la solicitud elegida en el perfil para abrir su comprobante
    fun verDetalleSolicitud(solicitud: SolicitudEntity) {
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

    // guarda la solicitud en sqlite y devuelve el folio generado para confirmar o notificar
    fun guardarEnSqlite(hayInternet: Boolean, onGuardado: (String) -> Unit = {}) {
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
            // guarda en la base de datos y arma un folio correlativo unico como al-2026-0001
            val nuevoId = solicitudDao.insertarSolicitud(solicitudInicial)
            val folioGenerado = String.format(Locale.getDefault(), "AL-2026-%04d", nuevoId)
            val solicitudFinal = solicitudInicial.copy(id = nuevoId.toInt(), folio = folioGenerado)
            solicitudDao.actualizarSolicitud(solicitudFinal)
            _solicitudSeleccionada.value = solicitudFinal

            // borra lo que habia en los campos del formulario para dejarlo limpio
            limpiarFormularioContacto()

            onGuardado(folioGenerado)
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

    // busca las solicitudes que se guardaron sin internet y las cambia a estado recibida
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

    // borra una solicitud de la base de datos cuando el usuario confirma en el dialogo
    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            solicitudDao.eliminarSolicitud(solicitud)
            if (_solicitudSeleccionada.value?.id == solicitud.id) {
                _solicitudSeleccionada.value = null
            }
        }
    }

    // actualiza los datos editados de una solicitud ya guardada en sqlite
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
            solicitudDao.actualizarSolicitud(solicitudActualizada)
            _solicitudSeleccionada.value = solicitudActualizada
        }
    }
}
