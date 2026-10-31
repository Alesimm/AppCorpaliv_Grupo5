package com.duoc.corpaliv.validation

// pruebas automaticas para verificar que las reglas de validacion funcionen bien
// se ejecutan en la computadora con junit 4 sin necesitar un celular conectado

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// grupo de pruebas unitarias que verifican correos, claves y ruts
class ValidadoresTest {

    // prueba 1: revisa que un correo bien escrito sea aceptado sin errores
    @Test
    fun validarCorreo_conCorreoValido_retornaNull() {
        val resultado = validarCorreo("colaborador@ejemplo.cl")
        assertNull(resultado)
    }

    // prueba 2: revisa que un correo sin arroba sea rechazado con mensaje de error
    @Test
    fun validarCorreo_conCorreoInvalido_retornaError() {
        val resultado = validarCorreo("correo-sin-arroba")
        assertNotNull(resultado)
        assertEquals("Ingresa un correo valido (ej: nombre@dominio.cl)", resultado)
    }

    // prueba 3: revisa que una clave corta de menos de 8 letras sea rechazada
    @Test
    fun validarClave_conClaveCorta_retornaError() {
        val resultado = validarClave("Alba1")
        assertNotNull(resultado)
        assertEquals("La contrasena debe tener minimo 8 caracteres", resultado)
    }

    // prueba 4: revisa que un rut con guion y digito verificador sea aceptado
    @Test
    fun validarRut_conRutValidoFicticio_retornaNull() {
        val resultado = validarRut("11.111.111-1")
        assertNull(resultado)
    }

    // prueba 5: revisa que un rut incompleto sin guion sea rechazado
    @Test
    fun validarRut_conRutInvalido_retornaError() {
        val resultado = validarRut("12345")
        assertNotNull(resultado)
        assertEquals("Formato de rut invalido (ej: 12345678-5)", resultado)
    }

    // prueba 6: revisa que el login acepte solo las credenciales correctas
    @Test
    fun validarCredencialesLogin_conDatosCorrectosEIncorrectos() {
        val correcto = validarCredencialesLogin("colaborador@ejemplo.cl", "Alba2026!")
        assertNull(correcto)

        val claveErronea = validarCredencialesLogin("colaborador@ejemplo.cl", "ClaveErronea1")
        assertEquals("Correo o clave incorrectos", claveErronea)
    }
}
