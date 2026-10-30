package com.duoc.corpaliv.model

import com.duoc.corpaliv.validation.CredencialesPrueba

// estructura inmutable que agrupa el estado de autenticacion y el formulario de contacto
// se conecta con usuarioviewmodel y es observada por loginscreen, homescreen, contactscreen y perfilscreen
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
