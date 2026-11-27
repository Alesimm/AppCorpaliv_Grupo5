package com.duoc.corpaliv.data

// archivo de pruebas unitarias para SolicitudRepository (arquitectura hibrida room + retrofit)
// usa mockk para simular SolicitudDao (sqlite local) y SolicitudApiService (microservicio spring boot)
// verifica que ante caida de red la app nunca pierda datos y marque como pendiente sin red

import com.duoc.corpaliv.remote.SolicitudApiService
import com.duoc.corpaliv.remote.SolicitudDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SolicitudRepositoryTest {

    private lateinit var dao: SolicitudDao
    private lateinit var api: SolicitudApiService
    private lateinit var repository: SolicitudRepository

    private val fichaEjemplo = SolicitudEntity(
        id = 1,
        folio = "ALBA-0001",
        fecha = "26/11/2026",
        colaboracion = "Donación",
        programa = "Taller de Oficios",
        nombre = "Ana",
        apellido = "Pérez",
        correo = "ana@ejemplo.cl",
        rutComercial = "No aplica",
        mensaje = "Apoyo mensual",
        estadoEnvio = "RECIBIDA"
    )

    // crea mocks relajados del dao y la api antes de cada prueba
    @Before
    fun configurar() {
        dao = mockk(relaxed = true)
        api = mockk(relaxed = true)
        every { dao.obtenerSolicitudes() } returns flowOf(emptyList())
        repository = SolicitudRepository(dao, api)
    }

    // --- refrescarDesdeServidor ---

    // simula que el servidor devuelve 1 solicitud y verifica que se guarde en room retornando true
    @Test
    fun refrescarDesdeServidor_caminoFeliz1_descargaYGuardaEnRoom() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()
        coEvery { api.listarSolicitudes() } returns listOf(
            SolicitudDto(id = 10L, folio = "ALBA-0010", nombre = "Ana", apellido = "Pérez", correo = "ana@ejemplo.cl")
        )

        val ok = repository.refrescarDesdeServidor()

        assertTrue(ok)
        coVerify(exactly = 1) { dao.insertarTodas(match { it.size == 1 && it.first().id == 10 }) }
    }

    // si el servidor responde lista vacia, retorna true sin llamar a insertarTodas en room
    @Test
    fun refrescarDesdeServidor_caminoFeliz2_listaVaciaRetornaTrueSinInsertar() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()
        coEvery { api.listarSolicitudes() } returns emptyList()

        val ok = repository.refrescarDesdeServidor()

        assertTrue(ok)
        coVerify(exactly = 0) { dao.insertarTodas(any()) }
    }

    // si el servidor esta caido al refrescar, atrapa la excepcion y retorna false sin romper la app
    @Test
    fun refrescarDesdeServidor_caminoMalo_servidorCaidoRetornaFalse() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()
        coEvery { api.listarSolicitudes() } throws RuntimeException("Sin conexion")

        val ok = repository.refrescarDesdeServidor()

        assertFalse(ok)
    }

    // --- crearSolicitud ---

    // con internet: envia post a la api, recibe id/folio del servidor, normaliza estado a RECIBIDA y guarda en room
    @Test
    fun crearSolicitud_caminoFeliz1_conInternetCreaEnApiYRoom() = runTest {
        val dtoCreado = SolicitudDto(
            id = 5L,
            folio = "ALBA-0005",
            nombre = "Ana",
            apellido = "Pérez",
            correo = "ana@ejemplo.cl",
            estadoEnvio = "Enviada"
        )
        coEvery { api.crearSolicitud(any()) } returns dtoCreado

        val resultado = repository.crearSolicitud(fichaEjemplo.copy(id = 0), hayInternet = true)

        assertEquals(5, resultado.id)
        assertEquals("ALBA-0005", resultado.folio)
        assertEquals("RECIBIDA", resultado.estadoEnvio)
        coVerify(exactly = 1) { dao.insertarSolicitud(resultado) }
    }

    // sin internet: no llama a la api, guarda directo en room con folio local y estado PENDIENTE SIN RED
    @Test
    fun crearSolicitud_caminoFeliz2_sinInternetGuardaPendienteEnRoom() = runTest {
        coEvery { dao.insertarSolicitud(any()) } returns 4L

        val resultado = repository.crearSolicitud(fichaEjemplo.copy(id = 0), hayInternet = false)

        assertEquals(4, resultado.id)
        assertEquals("AL-2026-0004", resultado.folio)
        assertEquals("PENDIENTE SIN RED", resultado.estadoEnvio)
        coVerify(exactly = 0) { api.crearSolicitud(any()) }
    }

    // con internet pero api caida (timeout): hace fallback a room y la marca PENDIENTE SIN RED para sincronizar luego
    @Test
    fun crearSolicitud_caminoMalo_conInternetPeroApiCaidaUsaRespaldoRoom() = runTest {
        coEvery { api.crearSolicitud(any()) } throws RuntimeException("Timeout")
        coEvery { dao.insertarSolicitud(any()) } returns 8L

        val resultado = repository.crearSolicitud(fichaEjemplo.copy(id = 0), hayInternet = true)

        assertEquals("PENDIENTE SIN RED", resultado.estadoEnvio)
        assertEquals("AL-2026-0008", resultado.folio)
    }

    // --- actualizarSolicitud ---

    // con id positivo: actualiza en room y envia put al microservicio
    @Test
    fun actualizarSolicitud_caminoFeliz1_actualizaRoomYEnviaPut() = runTest {
        coEvery { api.actualizarSolicitud(1L, any()) } returns SolicitudDto(id = 1L)

        val resultado = repository.actualizarSolicitud(fichaEjemplo)

        assertEquals(fichaEjemplo, resultado)
        coVerify(exactly = 1) { dao.actualizarSolicitud(fichaEjemplo) }
        coVerify(exactly = 1) { api.actualizarSolicitud(1L, any()) }
    }

    // con id=0 (registro solo local): actualiza room y omite el put a la api
    @Test
    fun actualizarSolicitud_caminoFeliz2_idCeroSoloActualizaRoom() = runTest {
        val local = fichaEjemplo.copy(id = 0)

        repository.actualizarSolicitud(local)

        coVerify(exactly = 1) { dao.actualizarSolicitud(local) }
        coVerify(exactly = 0) { api.actualizarSolicitud(any(), any()) }
    }

    // si falla el put (error 500): no se cae y conserva el cambio guardado localmente en room
    @Test
    fun actualizarSolicitud_caminoMalo_errorPutNoRompeYConservaCambioEnRoom() = runTest {
        coEvery { api.actualizarSolicitud(1L, any()) } throws RuntimeException("Error 500")

        val resultado = repository.actualizarSolicitud(fichaEjemplo)

        assertEquals(fichaEjemplo, resultado)
        coVerify(exactly = 1) { dao.actualizarSolicitud(fichaEjemplo) }
    }

    // --- eliminarSolicitud ---

    // con id positivo y servidor activo: envia delete a la api, borra en room y retorna true
    @Test
    fun eliminarSolicitud_caminoFeliz1_borraEnServidorYRoom() = runTest {
        coEvery { api.eliminarSolicitud(1L) } returns Response.success(Unit)

        val borradaEnServidor = repository.eliminarSolicitud(fichaEjemplo)

        assertTrue(borradaEnServidor)
        coVerify(exactly = 1) { dao.eliminarSolicitud(fichaEjemplo) }
    }

    // con id=0: borra solo en room sin llamar a delete en la api y retorna false
    @Test
    fun eliminarSolicitud_caminoFeliz2_idCeroSoloEliminaEnRoom() = runTest {
        val local = fichaEjemplo.copy(id = 0)

        val borradaEnServidor = repository.eliminarSolicitud(local)

        assertFalse(borradaEnServidor)
        coVerify(exactly = 0) { api.eliminarSolicitud(any()) }
        coVerify(exactly = 1) { dao.eliminarSolicitud(local) }
    }

    // si falla la red al borrar en servidor: retorna false pero igualmente elimina el registro local en room
    @Test
    fun eliminarSolicitud_caminoMalo_errorDeRedRetornaFalsePeroEliminaEnRoom() = runTest {
        coEvery { api.eliminarSolicitud(1L) } throws RuntimeException("Sin red")

        val borradaEnServidor = repository.eliminarSolicitud(fichaEjemplo)

        assertFalse(borradaEnServidor)
        coVerify(exactly = 1) { dao.eliminarSolicitud(fichaEjemplo) }
    }

    // --- sincronizarPendientes ---

    // toma 2 fichas pendientes, las sube por post a la api, reemplaza las locales por RECIBIDA y retorna 2
    @Test
    fun sincronizarPendientes_caminoFeliz1_subePendientesYRetornaCantidad() = runTest {
        val p1 = fichaEjemplo.copy(id = 1, estadoEnvio = "PENDIENTE SIN RED")
        val p2 = fichaEjemplo.copy(id = 2, estadoEnvio = "PENDIENTE SIN RED")
        coEvery { dao.obtenerPendientes() } returns listOf(p1, p2)
        coEvery { api.crearSolicitud(any()) } returns SolicitudDto(id = 20L, folio = "ALBA-0020")

        val enviadas = repository.sincronizarPendientes()

        assertEquals(2, enviadas)
        coVerify(exactly = 2) { dao.eliminarSolicitud(any()) }
        coVerify(exactly = 2) { dao.insertarSolicitud(match { it.estadoEnvio == "RECIBIDA" }) }
    }

    // si no hay solicitudes pendientes en room, retorna 0 sin hacer llamadas a la api
    @Test
    fun sincronizarPendientes_caminoFeliz2_sinPendientesRetornaCero() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()

        val enviadas = repository.sincronizarPendientes()

        assertEquals(0, enviadas)
        coVerify(exactly = 0) { api.crearSolicitud(any()) }
    }

    // si el servidor sigue apagado al intentar sincronizar, retorna 0 y no borra ninguna pendiente de room
    @Test
    fun sincronizarPendientes_caminoMalo_servidorApagadoNoApruebaNinguna() = runTest {
        val p1 = fichaEjemplo.copy(id = 1, estadoEnvio = "PENDIENTE SIN RED")
        coEvery { dao.obtenerPendientes() } returns listOf(p1)
        coEvery { api.crearSolicitud(any()) } throws RuntimeException("Servidor apagado")

        val enviadas = repository.sincronizarPendientes()

        assertEquals(0, enviadas)
        coVerify(exactly = 0) { dao.eliminarSolicitud(any()) }
    }
}
