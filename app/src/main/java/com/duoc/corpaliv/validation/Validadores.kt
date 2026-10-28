package com.duoc.corpaliv.validation

// funciones de validacion desacopladas de la interfaz grafica
// se conecta con usuarioviewmodel y con los tests unitarios en src/test

// credenciales de prueba centralizadas para el acceso a la aplicacion
object CredencialesPrueba {
    const val CORREO = "colaborador@ejemplo.cl"
    const val CLAVE = "Alba2026!"
}

// valida que el correo no este vacio y cumpla con el formato estandar
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

// valida longitud minima de 8 caracteres y que combine letras con numeros
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

// valida que el nombre tenga al menos 3 letras y no incluya numeros
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

// valida que el rut comercial tenga formato chileno valido con guion
fun validarRut(rut: String): String? {
    val limpio = rut.replace(".", "").trim().uppercase()
    if (limpio.isEmpty()) {
        return "Ingresa el rut comercial"
    }
    // verifica de 7 a 8 digitos, guion y digito verificador (numero o letra K)
    val regex = Regex("^[0-9]{7,8}-[0-9K]$")
    if (!regex.matches(limpio)) {
        return "Formato de rut invalido (ej: 12345678-5)"
    }
    return null
}

// valida que se haya seleccionado un tipo de colaboracion
fun validarColaboracion(colaboracion: String): String? {
    if (colaboracion.trim().isEmpty()) {
        return "Selecciona como quieres ayudar"
    }
    return null
}

// valida que el mensaje opcional no exceda el limite de 200 caracteres
fun validarMensaje(mensaje: String): String? {
    if (mensaje.length > 200) {
        return "El mensaje no puede superar los 200 caracteres"
    }
    return null
}

// comprueba que las credenciales coincidan con el usuario de prueba
fun validarCredencialesLogin(correo: String, clave: String): String? {
    val correoCoincide = correo.trim().equals(CredencialesPrueba.CORREO, ignoreCase = true)
    val claveCoincide = clave == CredencialesPrueba.CLAVE
    if (!correoCoincide || !claveCoincide) {
        return "Correo o clave incorrectos"
    }
    return null
}
