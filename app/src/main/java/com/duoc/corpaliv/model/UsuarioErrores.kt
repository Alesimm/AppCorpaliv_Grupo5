package com.duoc.corpaliv.model

// guarda los mensajes de error en rojo que aparecen debajo de cada campo del formulario
// si un campo esta bien escrito su valor es null y no muestra nada en la pantalla
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val colaboracion: String? = null,
    val rutComercial: String? = null,
    val mensaje: String? = null
)
