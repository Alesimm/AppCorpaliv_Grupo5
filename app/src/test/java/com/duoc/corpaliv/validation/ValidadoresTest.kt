package com.duoc.corpaliv.validation

// archivo de pruebas unitarias para todas las reglas de validacion de formularios y login
// cada funcion devuelve null cuando el dato es valido, o un string con el mensaje de error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    // --- validarCorreo ---

    // correo valido comun: espera null (sin error)
    @Test
    fun validarCorreo_caminoFeliz1_correoEstandarValido() {
        assertNull(validarCorreo("colaborador@ejemplo.cl"))
    }

    // correo con espacios en bordes y subdominio: espera que haga trim y retorne null
    @Test
    fun validarCorreo_caminoFeliz2_correoConEspaciosYSubdominio() {
        assertNull(validarCorreo("  ana.perez+test@equipo.ejemplo.cl  "))
    }

    // correo sin arroba ni dominio: espera el mensaje exacto de formato invalido
    @Test
    fun validarCorreo_caminoMalo_sinArrobaRetornaError() {
        val resultado = validarCorreo("correo-sin-arroba")
        assertEquals("El formato del correo electrónico no es válido (ej: nombre@dominio.cl).", resultado)
    }

    // --- validarClave ---

    // clave valida de registro con letras, numeros y minimo 8 caracteres: espera null
    @Test
    fun validarClave_caminoFeliz1_letrasYNumerosMinimo8() {
        assertNull(validarClave("Alba2026"))
    }

    // clave valida con simbolo adicional: espera null
    @Test
    fun validarClave_caminoFeliz2_conSimbolosYMayusculas() {
        assertNull(validarClave("Alba2026!"))
    }

    // clave demasiado corta (<8): espera mensaje de longitud minima
    @Test
    fun validarClave_caminoMalo_menorA8Caracteres() {
        assertEquals("La contraseña debe tener mínimo 8 caracteres.", validarClave("Alba1"))
    }

    // --- validarClaveLogin ---

    // clave no vacia en login: espera null
    @Test
    fun validarClaveLogin_caminoFeliz1_claveConTexto() {
        assertNull(validarClaveLogin("Alba2026!"))
    }

    // en login no se dan pistas de politica de clave aunque sea corta, solo que no este vacia
    @Test
    fun validarClaveLogin_caminoFeliz2_claveCortaNoVaciaNoDaPistas() {
        assertNull(validarClaveLogin("123"))
    }

    // clave vacia en login: espera mensaje pidiendo ingresar contraseña
    @Test
    fun validarClaveLogin_caminoMalo_vaciaRetornaError() {
        assertEquals("Por favor, ingresa tu contraseña.", validarClaveLogin(""))
    }

    // --- validarNombre ---

    // nombre simple solo con letras: espera null
    @Test
    fun validarNombre_caminoFeliz1_nombreSimple() {
        assertNull(validarNombre("Camila"))
    }

    // nombre compuesto con tilde y espacio: espera null
    @Test
    fun validarNombre_caminoFeliz2_nombreCompuestoConTilde() {
        assertNull(validarNombre("María José"))
    }

    // nombre con simbolos (@!): espera rechazo con mensaje de caracteres especiales
    @Test
    fun validarNombre_caminoMalo_caracteresEspecialesRechazados() {
        assertEquals("El nombre no puede contener caracteres especiales", validarNombre("Ana@!"))
    }

    // --- validarApellido ---

    // apellido simple: espera null
    @Test
    fun validarApellido_caminoFeliz1_apellidoSimple() {
        assertNull(validarApellido("Rojas"))
    }

    // apellido compuesto con ñ y tilde: espera null
    @Test
    fun validarApellido_caminoFeliz2_apellidoCompuestoConEneYTilde() {
        assertNull(validarApellido("Núñez Peña"))
    }

    // apellido con punto o simbolo: espera mensaje de caracteres especiales rechazados
    @Test
    fun validarApellido_caminoMalo_caracteresEspecialesRechazados() {
        assertEquals("El apellido no puede contener caracteres especiales", validarApellido("Pérez."))
    }

    // --- validarRut ---

    // rut con puntos y guion: espera null
    @Test
    fun validarRut_caminoFeliz1_conPuntosYGuion() {
        assertNull(validarRut("11.111.111-1"))
    }

    // rut sin puntos terminado en k minuscula: espera null
    @Test
    fun validarRut_caminoFeliz2_sinPuntosYDigitoK() {
        assertNull(validarRut("12345678-k"))
    }

    // rut incompleto sin guion ni digito verificador: espera mensaje de formato invalido
    @Test
    fun validarRut_caminoMalo_incompletoSinGuion() {
        assertEquals("Formato de rut invalido (ej: 12345678-5)", validarRut("12345"))
    }

    // --- validarColaboracion ---

    // tipo de colaboracion Donacion: espera null
    @Test
    fun validarColaboracion_caminoFeliz1_donacion() {
        assertNull(validarColaboracion("Donación"))
    }

    // tipo de colaboracion Alianza empresarial: espera null
    @Test
    fun validarColaboracion_caminoFeliz2_alianzaEmpresarial() {
        assertNull(validarColaboracion("Alianza empresarial"))
    }

    // seleccion vacia o solo espacios: espera mensaje pidiendo seleccionar como ayudar
    @Test
    fun validarColaboracion_caminoMalo_vacioRetornaError() {
        assertEquals("Selecciona como quieres ayudar", validarColaboracion("   "))
    }

    // --- validarMensaje ---

    // mensaje vacio es valido porque el campo es opcional: espera null
    @Test
    fun validarMensaje_caminoFeliz1_vacioEsOpcional() {
        assertNull(validarMensaje(""))
    }

    // mensaje dentro del limite de 200 caracteres: espera null
    @Test
    fun validarMensaje_caminoFeliz2_dentroDelLimite200() {
        assertNull(validarMensaje("Me gustaria apoyar el taller con materiales."))
    }

    // mensaje de 201 caracteres supera el maximo: espera error de limite
    @Test
    fun validarMensaje_caminoMalo_supera200Caracteres() {
        val textoLargo = "a".repeat(201)
        assertEquals("El mensaje no puede superar los 200 caracteres", validarMensaje(textoLargo))
    }

    // --- validarCredencialesLogin ---

    // credenciales exactas del usuario ficticio de prueba: espera null
    @Test
    fun validarCredencialesLogin_caminoFeliz1_credencialesExactas() {
        assertNull(validarCredencialesLogin("colaborador@ejemplo.cl", "Alba2026!"))
    }

    // correo en mayusculas y con espacios: espera que normalice el correo y retorne null
    @Test
    fun validarCredencialesLogin_caminoFeliz2_correoConMayusculasYEspacios() {
        assertNull(validarCredencialesLogin("  COLABORADOR@EJEMPLO.CL ", "Alba2026!"))
    }

    // clave erronea: espera mensaje generico sin revelar si fallo el correo o la clave
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
