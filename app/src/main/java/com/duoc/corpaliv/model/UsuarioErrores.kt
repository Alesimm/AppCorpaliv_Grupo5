package com.duoc.corpaliv.model

// estructura que almacena los mensajes de validacion del formulario de contacto
// se conecta con usuarioviewmodel, validadores y contactscreen
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val colaboracion: String? = null,
    val rutComercial: String? = null,
    val mensaje: String? = null
)
