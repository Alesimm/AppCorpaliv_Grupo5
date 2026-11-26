package com.duoc.corpaliv.api.model

// entidad jpa que representa la tabla solicitudes en h2
// usa los mismos nombres de campos que SolicitudEntity en la app android

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "solicitudes")
class Solicitud(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var folio: String = "",
    var fecha: String = "",

    @field:NotBlank(message = "El tipo de colaboración es obligatorio.")
    var colaboracion: String = "",

    @field:NotBlank(message = "El programa es obligatorio.")
    var programa: String = "",

    @field:NotBlank(message = "El nombre es obligatorio.")
    var nombre: String = "",

    @field:NotBlank(message = "El apellido es obligatorio.")
    var apellido: String = "",

    @field:NotBlank(message = "El correo es obligatorio.")
    @field:Email(message = "El formato del correo electrónico no es válido.")
    var correo: String = "",

    var rutComercial: String = "",

    @field:Size(max = 200, message = "El mensaje no puede superar los 200 caracteres.")
    @Column(length = 250)
    var mensaje: String = "",

    var estadoEnvio: String = ""
)
