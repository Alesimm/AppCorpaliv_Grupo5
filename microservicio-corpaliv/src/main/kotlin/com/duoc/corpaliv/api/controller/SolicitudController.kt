package com.duoc.corpaliv.api.controller

// controlador rest que expone el crud de fichas de contacto en /api/v1/solicitudes

import com.duoc.corpaliv.api.model.Solicitud
import com.duoc.corpaliv.api.service.SolicitudService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/solicitudes")
class SolicitudController(
    private val servicioSolicitudes: SolicitudService
) {

    // lista todas las fichas desde la mas reciente
    @GetMapping
    fun listar(): List<Solicitud> {
        return servicioSolicitudes.consultarHistorialAlba()
    }

    // devuelve una ficha por id o 404
    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): Solicitud {
        return servicioSolicitudes.obtenerFichaPorId(id)
    }

    // crea una ficha nueva, @Valid responde 400 si faltan datos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody ficha: Solicitud): Solicitud {
        return servicioSolicitudes.guardarFichaContacto(ficha)
    }

    // actualiza una ficha existente o 404
    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody ficha: Solicitud): Solicitud {
        return servicioSolicitudes.cambiarModalidadApoyo(id, ficha)
    }

    // elimina una ficha y responde 204 sin cuerpo
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) {
        servicioSolicitudes.retirarFichaContacto(id)
    }
}
