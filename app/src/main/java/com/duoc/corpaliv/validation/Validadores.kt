package com.duoc.corpaliv.validation

// reglas para revisar que lo que escribe el usuario este bien escrito
// son funciones independientes de la pantalla que se prueban con tests unitarios

// correo y contrasena oficiales que vienen listos para probar la aplicacion
object CredencialesPrueba {
    const val CORREO = "colaborador@ejemplo.cl"
    const val CLAVE = "Alba2026!"
}

// revisa que el correo no este vacio y tenga arroba y punto
fun validarCorreo(correo: String): String? {
    val texto = correo.trim()
    if (texto.isEmpty()) {
        return "Ingresa tu correo electronico"
    }
    val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    if (!regex.matches(texto)) {
        return "Ingresa un correo valido (ej: nombre@dominio.cl)"
    }
    return null
}

// revisa que la clave tenga al menos 8 letras o numeros y combine ambos
fun validarClave(clave: String): String? {
    if (clave.isEmpty()) {
        return "Ingresa tu contrasena"
    }
    if (clave.length < 8) {
        return "La contrasena debe tener minimo 8 caracteres"
    }
    val tieneLetra = clave.any { it.isLetter() }
    val tieneNumero = clave.any { it.isDigit() }
    if (!tieneLetra || !tieneNumero) {
        return "La contrasena debe incluir al menos una letra y un numero"
    }
    return null
}

// revisa que el nombre tenga minimo 3 letras y no tenga numeros
fun validarNombre(nombre: String): String? {
    val texto = nombre.trim()
    if (texto.isEmpty()) {
        return "Ingresa tu nombre y apellido"
    }
    if (texto.length < 3) {
        return "El nombre debe tener al menos 3 caracteres"
    }
    if (texto.any { it.isDigit() }) {
        return "El nombre no puede contener numeros"
    }
    return null
}

// revisa que el rut de empresa tenga formato chileno valido con guion
fun validarRut(rut: String): String? {
    val limpio = rut.replace(".", "").trim().uppercase()
    if (limpio.isEmpty()) {
        return "Ingresa el rut comercial"
    }
    // acepta de 7 a 8 numeros antes del guion y un digito o letra k al final
    val regex = Regex("^[0-9]{7,8}-[0-9K]$")
    if (!regex.matches(limpio)) {
        return "Formato de rut invalido (ej: 12345678-5)"
    }
    return null
}

// revisa que el usuario haya marcado una forma de colaborar de la lista
fun validarColaboracion(colaboracion: String): String? {
    if (colaboracion.trim().isEmpty()) {
        return "Selecciona como quieres ayudar"
    }
    return null
}

// revisa que el mensaje escrito no tenga mas de 200 caracteres
fun validarMensaje(mensaje: String): String? {
    if (mensaje.length > 200) {
        return "El mensaje no puede superar los 200 caracteres"
    }
    return null
}

// compara el correo y la clave ingresados contra el usuario de prueba
fun validarCredencialesLogin(correo: String, clave: String): String? {
    val correoCoincide = correo.trim().equals(CredencialesPrueba.CORREO, ignoreCase = true)
    val claveCoincide = clave == CredencialesPrueba.CLAVE
    if (!correoCoincide || !claveCoincide) {
        return "Correo o clave incorrectos"
    }
    return null
}
