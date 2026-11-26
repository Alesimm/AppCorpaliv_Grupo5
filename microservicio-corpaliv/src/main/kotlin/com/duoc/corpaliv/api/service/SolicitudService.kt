package com.duoc.corpaliv.api.service

// servicio que concentra las reglas de negocio de las fichas de contacto albalab

import com.duoc.corpaliv.api.model.Solicitud
import com.duoc.corpaliv.api.repository.SolicitudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SolicitudService(
    private val repoSolicitudes: SolicitudRepository
) {

    // obtiene todas las fichas registradas desde la mas reciente
    fun consultarHistorialAlba(): List<Solicitud> {
        return repoSolicitudes.findAllByOrderByIdDesc()
    }

    // busca una ficha por id o responde 404 si no existe
    fun obtenerFichaPorId(idFicha: Long): Solicitud {
        return repoSolicitudes.findById(idFicha).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Ficha no encontrada")
        }
    }

    // guarda una nueva ficha y completa folio, fecha o estado si vienen vacios
    fun guardarFichaContacto(fichaNueva: Solicitud): Solicitud {
        fichaNueva.id = null
        if (fichaNueva.folio.isBlank()) {
            val sufijo = (System.currentTimeMillis() % 10000).toString().padStart(4, '0')
            fichaNueva.folio = "ALBA-$sufijo"
        }
        if (fichaNueva.fecha.isBlank()) {
            fichaNueva.fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
        if (fichaNueva.estadoEnvio.isBlank()) {
            fichaNueva.estadoEnvio = "Enviada"
        }
        return repoSolicitudes.save(fichaNueva)
    }

    // actualiza los datos editables de una ficha existente o responde 404 si no existe
    fun cambiarModalidadApoyo(idFicha: Long, datosNuevos: Solicitud): Solicitud {
        val fichaActual = obtenerFichaPorId(idFicha)

        // folio y fecha se mantienen, solo cambian los datos editables
        fichaActual.colaboracion = datosNuevos.colaboracion
        fichaActual.programa = datosNuevos.programa
        fichaActual.nombre = datosNuevos.nombre
        fichaActual.apellido = datosNuevos.apellido
        fichaActual.correo = datosNuevos.correo
        fichaActual.rutComercial = datosNuevos.rutComercial
        fichaActual.mensaje = datosNuevos.mensaje
        if (datosNuevos.estadoEnvio.isNotBlank()) {
            fichaActual.estadoEnvio = datosNuevos.estadoEnvio
        }

        return repoSolicitudes.save(fichaActual)
    }

    // elimina una ficha por id o responde 404 si ya no existe en h2
    fun retirarFichaContacto(idFicha: Long) {
        if (!repoSolicitudes.existsById(idFicha)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Ficha no encontrada")
        }
        repoSolicitudes.deleteById(idFicha)
    }
}
