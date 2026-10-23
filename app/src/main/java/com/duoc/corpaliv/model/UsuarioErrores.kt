package com.duoc.corpaliv.model

// modelo que guarda los mensajes de error individuales de los formularios
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null,
    val colaboracion: String? = null,
    val rutComercial: String? = null
)