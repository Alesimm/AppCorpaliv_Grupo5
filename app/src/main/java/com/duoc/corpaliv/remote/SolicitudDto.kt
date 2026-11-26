package com.duoc.corpaliv.remote

// modelo json que representa una solicitud al comunicarse con el microservicio spring boot

import com.duoc.corpaliv.data.SolicitudEntity

data class SolicitudDto(
    val id: Long? = null,
    val folio: String = "",
    val fecha: String = "",
    val colaboracion: String = "",
    val programa: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val rutComercial: String = "",
    val mensaje: String = "",
    val estadoEnvio: String = ""
)

// convierte la respuesta del servidor al formato de la tabla local room
fun SolicitudDto.toEntity(): SolicitudEntity {
    return SolicitudEntity(
        id = (id ?: 0L).toInt(),
        folio = folio,
        fecha = fecha,
        colaboracion = colaboracion,
        programa = programa,
        nombre = nombre,
        apellido = apellido,
        correo = correo,
        rutComercial = rutComercial,
        mensaje = mensaje,
        estadoEnvio = estadoEnvio.ifBlank { "RECIBIDA" }
    )
}

// convierte una solicitud local room al formato json que espera el microservicio
fun SolicitudEntity.toDto(incluirId: Boolean = true): SolicitudDto {
    return SolicitudDto(
        id = if (incluirId && id > 0) id.toLong() else null,
        folio = folio,
        fecha = fecha,
        colaboracion = colaboracion,
        programa = programa,
        nombre = nombre,
        apellido = apellido,
        correo = correo,
        rutComercial = rutComercial,
        mensaje = mensaje,
        estadoEnvio = estadoEnvio
    )
}
