package com.duoc.corpaliv.model

// estructura que almacena los mensajes de error de los formularios
// se conecta con usuarioviewmodel, validadores y las pantallas de login y contacto
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null,
    val colaboracion: String? = null,
    val rutComercial: String? = null,
    val mensaje: String? = null
)