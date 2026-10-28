package com.duoc.corpaliv.validation

// pruebas unitarias para validar las funciones puras desacopladas de la interfaz
// se conecta con validadores.kt y se ejecuta con gradlew testdebugunittest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    // test 1: comprueba que un correo bien estructurado sea aceptado
    @Test
    fun validarCorreo_conCorreoValido_retornaNull() {
        val resultado = validarCorreo("colaborador@ejemplo.cl")
        assertNull(resultado)
    }

    // test 2: comprueba que un correo sin formato sea rechazado con mensaje
    @Test
    fun validarCorreo_conCorreoInvalido_retornaError() {
        val resultado = validarCorreo("correo-sin-arroba")
        assertNotNull(resultado)
        assertEquals("Ingresa un correo valido (ej: nombre@dominio.cl)", resultado)
    }

    // test 3: comprueba que una clave de menos de 8 caracteres sea rechazada
    @Test
    fun validarClave_conClaveCorta_retornaError() {
        val resultado = validarClave("Alba1")
        assertNotNull(resultado)
        assertEquals("La contrasena debe tener minimo 8 caracteres", resultado)
    }

    // test 4: comprueba que un rut ficticio con formato correcto sea aceptado
    @Test
    fun validarRut_conRutValidoFicticio_retornaNull() {
        val resultado = validarRut("11.111.111-1")
        assertNull(resultado)
    }

    // test 5: comprueba que un rut sin formato chileno sea rechazado
    @Test
    fun validarRut_conRutInvalido_retornaError() {
        val resultado = validarRut("12345")
        assertNotNull(resultado)
        assertEquals("Formato de rut invalido (ej: 12345678-5)", resultado)
    }

    // test adicional: comprueba la autenticacion contra el usuario de prueba
    @Test
    fun validarCredencialesLogin_conDatosCorrectosEIncorrectos() {
        val correcto = validarCredencialesLogin("colaborador@ejemplo.cl", "Alba2026!")
        assertNull(correcto)

        val claveErronea = validarCredencialesLogin("colaborador@ejemplo.cl", "ClaveErronea1")
        assertEquals("Correo o clave incorrectos", claveErronea)
    }
}
