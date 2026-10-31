package com.duoc.corpaliv.model

// guarda todos los datos actuales de la pantalla como el usuario activo y lo que escribe
// cuando algo cambia aqui las vistas de compose se redibujan solas al instante

import com.duoc.corpaliv.validation.CredencialesPrueba

// estado completo de la sesion del usuario y del formulario de contacto
data class UsuarioUiState(
    val correoLogin: String = CredencialesPrueba.CORREO,
    val claveLogin: String = CredencialesPrueba.CLAVE,
    val recordarCorreo: Boolean = false,
    val errorLoginCorreo: String? = null,
    val errorLoginClave: String? = null,
    val nombreUsuarioActivo: String = "",
    val correoUsuarioActivo: String = "",
    val nombre: String = "",
    val correo: String = "",
    val colaboracion: String = "",
    val programa: String = "",
    val rutComercial: String = "",
    val mensaje: String = "",
    val errores: UsuarioErrores = UsuarioErrores()
)
