package com.duoc.corpaliv.model

import com.duoc.corpaliv.validation.CredencialesPrueba

// Representa el estado inmutable del login, la sesión activa y el formulario de contacto.
// Se conecta con UsuarioViewModel y es observado por LoginScreen, HomeScreen, ContactScreen y PerfilScreen.
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
    val clave: String = "",
    val direccion: String = "",
    val colaboracion: String = "",
    val programa: String = "",
    val rutComercial: String = "",
    val mensaje: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores()
)
