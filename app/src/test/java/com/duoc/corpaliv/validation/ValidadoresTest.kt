package com.duoc.corpaliv.validation

// pruebas unitarias de todas las reglas de validacion del proyecto
// cada funcion se prueba con 2 caminos felices y 1 camino malo para facilitar la defensa

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    // --- validarCorreo (2 felices, 1 malo) ---

    @Test
    fun validarCorreo_caminoFeliz1_correoEstandarValido() {
        assertNull(validarCorreo("colaborador@ejemplo.cl"))
    }

    @Test
    fun validarCorreo_caminoFeliz2_correoConEspaciosYSubdominio() {
        assertNull(validarCorreo("  ana.perez+test@equipo.ejemplo.cl  "))
    }

    @Test
    fun validarCorreo_caminoMalo_sinArrobaRetornaError() {
        val resultado = validarCorreo("correo-sin-arroba")
        assertEquals("El formato del correo electrónico no es válido (ej: nombre@dominio.cl).", resultado)
    }

    // --- validarClave (2 felices, 1 malo) ---

    @Test
    fun validarClave_caminoFeliz1_letrasYNumerosMinimo8() {
        assertNull(validarClave("Alba2026"))
    }

    @Test
    fun validarClave_caminoFeliz2_conSimbolosYMayusculas() {
        assertNull(validarClave("Alba2026!"))
    }

    @Test
    fun validarClave_caminoMalo_menorA8Caracteres() {
        assertEquals("La contraseña debe tener mínimo 8 caracteres.", validarClave("Alba1"))
    }

    // --- validarClaveLogin (2 felices, 1 malo) ---

    @Test
    fun validarClaveLogin_caminoFeliz1_claveConTexto() {
        assertNull(validarClaveLogin("Alba2026!"))
    }

    @Test
    fun validarClaveLogin_caminoFeliz2_claveCortaNoVaciaNoDaPistas() {
        assertNull(validarClaveLogin("123"))
    }

    @Test
    fun validarClaveLogin_caminoMalo_vaciaRetornaError() {
        assertEquals("Por favor, ingresa tu contraseña.", validarClaveLogin(""))
    }

    // --- validarNombre (2 felices, 1 malo) ---

    @Test
    fun validarNombre_caminoFeliz1_nombreSimple() {
        assertNull(validarNombre("Camila"))
    }

    @Test
    fun validarNombre_caminoFeliz2_nombreCompuestoConTilde() {
        assertNull(validarNombre("María José"))
    }

    @Test
    fun validarNombre_caminoMalo_caracteresEspecialesRechazados() {
        assertEquals("El nombre no puede contener caracteres especiales", validarNombre("Ana@!"))
    }

    // --- validarApellido (2 felices, 1 malo) ---

    @Test
    fun validarApellido_caminoFeliz1_apellidoSimple() {
        assertNull(validarApellido("Rojas"))
    }

    @Test
    fun validarApellido_caminoFeliz2_apellidoCompuestoConEneYTilde() {
        assertNull(validarApellido("Núñez Peña"))
    }

    @Test
    fun validarApellido_caminoMalo_caracteresEspecialesRechazados() {
        assertEquals("El apellido no puede contener caracteres especiales", validarApellido("Pérez."))
    }

    // --- validarRut (2 felices, 1 malo) ---

    @Test
    fun validarRut_caminoFeliz1_conPuntosYGuion() {
        assertNull(validarRut("11.111.111-1"))
    }

    @Test
    fun validarRut_caminoFeliz2_sinPuntosYDigitoK() {
        assertNull(validarRut("12345678-k"))
    }

    @Test
    fun validarRut_caminoMalo_incompletoSinGuion() {
        assertEquals("Formato de rut invalido (ej: 12345678-5)", validarRut("12345"))
    }

    // --- validarColaboracion (2 felices, 1 malo) ---

    @Test
    fun validarColaboracion_caminoFeliz1_donacion() {
        assertNull(validarColaboracion("Donación"))
    }

    @Test
    fun validarColaboracion_caminoFeliz2_alianzaEmpresarial() {
        assertNull(validarColaboracion("Alianza empresarial"))
    }

    @Test
    fun validarColaboracion_caminoMalo_vacioRetornaError() {
        assertEquals("Selecciona como quieres ayudar", validarColaboracion("   "))
    }

    // --- validarMensaje (2 felices, 1 malo) ---

    @Test
    fun validarMensaje_caminoFeliz1_vacioEsOpcional() {
        assertNull(validarMensaje(""))
    }

    @Test
    fun validarMensaje_caminoFeliz2_dentroDelLimite200() {
        assertNull(validarMensaje("Me gustaria apoyar el taller con materiales."))
    }

    @Test
    fun validarMensaje_caminoMalo_supera200Caracteres() {
        val textoLargo = "a".repeat(201)
        assertEquals("El mensaje no puede superar los 200 caracteres", validarMensaje(textoLargo))
    }

    // --- validarCredencialesLogin (2 felices, 1 malo) ---

    @Test
    fun validarCredencialesLogin_caminoFeliz1_credencialesExactas() {
        assertNull(validarCredencialesLogin("colaborador@ejemplo.cl", "Alba2026!"))
    }

    @Test
    fun validarCredencialesLogin_caminoFeliz2_correoConMayusculasYEspacios() {
        assertNull(validarCredencialesLogin("  COLABORADOR@EJEMPLO.CL ", "Alba2026!"))
    }

    @Test
    fun validarCredencialesLogin_caminoMalo_claveErroneaMensajeGenerico() {
        val resultado = validarCredencialesLogin("colaborador@ejemplo.cl", "OtraClave1")
        assertNotNull(resultado)
        assertEquals(
            "Correo electrónico o contraseña incorrectos. Verifica tus credenciales.",
            resultado
        )
    }
}
