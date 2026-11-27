package com.duoc.corpaliv.data

// repositorio hibrido que coordina el microservicio spring boot con la base local room
// si el servidor no responde, sigue funcionando sin errores usando sqlite como respaldo

import com.duoc.corpaliv.remote.SolicitudApiService
import com.duoc.corpaliv.remote.toDto
import com.duoc.corpaliv.remote.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val api: SolicitudApiService
) {

    // candado para evitar envios duplicados si se sincroniza dos veces al mismo tiempo
    private val mutexSync = Mutex()

    // flujo reactivo desde room para que las pantallas se actualicen solas
    val solicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerSolicitudes()

    // sube primero las pendientes si el servidor responde y luego descarga el historial actualizado
    suspend fun refrescarDesdeServidor(): Boolean {
        return try {
            sincronizarPendientes()
            val remotas = api.listarSolicitudes().map { it.toEntity() }
            if (remotas.isNotEmpty()) {
                solicitudDao.insertarTodas(remotas)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    // intenta crear en el microservicio y si falla guarda localmente como pendiente
    suspend fun crearSolicitud(
        solicitudBase: SolicitudEntity,
        hayInternet: Boolean
    ): SolicitudEntity {
        if (hayInternet) {
            try {
                // sube pendientes antes de crear una nueva para evitar choques de ids locales
                sincronizarPendientes()
                val creadaRemota = api.crearSolicitud(
                    solicitudBase.copy(estadoEnvio = "RECIBIDA").toDto(incluirId = false)
                )
                val entidadSincronizada = creadaRemota.toEntity().copy(estadoEnvio = "RECIBIDA")
                solicitudDao.insertarSolicitud(entidadSincronizada)
                return entidadSincronizada
            } catch (_: Exception) {
                // si el servidor esta apagado cae al respaldo local sin romper el flujo
            }
        }

        val solicitudOffline = solicitudBase.copy(estadoEnvio = "PENDIENTE SIN RED")
        val nuevoId = solicitudDao.insertarSolicitud(solicitudOffline)
        val folioLocal = String.format(Locale.getDefault(), "AL-2026-%04d", nuevoId)
        val solicitudFinal = solicitudOffline.copy(id = nuevoId.toInt(), folio = folioLocal)
        solicitudDao.actualizarSolicitud(solicitudFinal)
        return solicitudFinal
    }

    // actualiza primero en room y sincroniza el cambio por put si ya existe en el microservicio
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity): SolicitudEntity {
        solicitudDao.actualizarSolicitud(solicitud)
        try {
            if (solicitud.id > 0 && !solicitud.estadoEnvio.startsWith("PENDIENTE")) {
                api.actualizarSolicitud(solicitud.id.toLong(), solicitud.toDto())
            }
        } catch (_: Exception) {
            // mantiene el cambio guardado en room aunque no haya conexion con el servidor
        }
        return solicitud
    }

    // borra en el microservicio si ya estaba sincronizada y siempre elimina el registro de room
    suspend fun eliminarSolicitud(solicitud: SolicitudEntity): Boolean {
        var borradaEnServidor = false
        try {
            if (solicitud.id > 0 && !solicitud.estadoEnvio.startsWith("PENDIENTE")) {
                val respuesta = api.eliminarSolicitud(solicitud.id.toLong())
                borradaEnServidor = respuesta.isSuccessful
            }
        } catch (_: Exception) {
            borradaEnServidor = false
        }
        solicitudDao.eliminarSolicitud(solicitud)
        return borradaEnServidor
    }

    // reenvia al servidor las solicitudes guardadas sin red y solo las aprueba si el post responde ok
    suspend fun sincronizarPendientes(): Int = mutexSync.withLock {
        val pendientes = solicitudDao.obtenerPendientes()
        var enviadas = 0
        for (pendiente in pendientes) {
            try {
                val creada = api.crearSolicitud(
                    pendiente.copy(estadoEnvio = "RECIBIDA").toDto(incluirId = false)
                )
                solicitudDao.eliminarSolicitud(pendiente)
                solicitudDao.insertarSolicitud(creada.toEntity().copy(estadoEnvio = "RECIBIDA"))
                enviadas++
            } catch (_: Exception) {
                // si el servidor sigue apagado mantiene el estado pendiente sin fallar
            }
        }
        enviadas
    }
}
