package com.duoc.corpaliv.data

// pruebas unitarias del repositorio hibrido usando mockk y corrutinas
// cada metodo se valida con 2 caminos felices y 1 camino malo (servidor caido o error de red)

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

    @Before
    fun configurar() {
        dao = mockk(relaxed = true)
        api = mockk(relaxed = true)
        every { dao.obtenerSolicitudes() } returns flowOf(emptyList())
        repository = SolicitudRepository(dao, api)
    }

    // --- refrescarDesdeServidor (2 felices, 1 malo) ---

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

    @Test
    fun refrescarDesdeServidor_caminoFeliz2_listaVaciaRetornaTrueSinInsertar() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()
        coEvery { api.listarSolicitudes() } returns emptyList()

        val ok = repository.refrescarDesdeServidor()

        assertTrue(ok)
        coVerify(exactly = 0) { dao.insertarTodas(any()) }
    }

    @Test
    fun refrescarDesdeServidor_caminoMalo_servidorCaidoRetornaFalse() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()
        coEvery { api.listarSolicitudes() } throws RuntimeException("Sin conexion")

        val ok = repository.refrescarDesdeServidor()

        assertFalse(ok)
    }

    // --- crearSolicitud (2 felices, 1 malo) ---

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

    @Test
    fun crearSolicitud_caminoFeliz2_sinInternetGuardaPendienteEnRoom() = runTest {
        coEvery { dao.insertarSolicitud(any()) } returns 4L

        val resultado = repository.crearSolicitud(fichaEjemplo.copy(id = 0), hayInternet = false)

        assertEquals(4, resultado.id)
        assertEquals("AL-2026-0004", resultado.folio)
        assertEquals("PENDIENTE SIN RED", resultado.estadoEnvio)
        coVerify(exactly = 0) { api.crearSolicitud(any()) }
    }

    @Test
    fun crearSolicitud_caminoMalo_conInternetPeroApiCaidaUsaRespaldoRoom() = runTest {
        coEvery { api.crearSolicitud(any()) } throws RuntimeException("Timeout")
        coEvery { dao.insertarSolicitud(any()) } returns 8L

        val resultado = repository.crearSolicitud(fichaEjemplo.copy(id = 0), hayInternet = true)

        assertEquals("PENDIENTE SIN RED", resultado.estadoEnvio)
        assertEquals("AL-2026-0008", resultado.folio)
    }

    // --- actualizarSolicitud (2 felices, 1 malo) ---

    @Test
    fun actualizarSolicitud_caminoFeliz1_actualizaRoomYEnviaPut() = runTest {
        coEvery { api.actualizarSolicitud(1L, any()) } returns SolicitudDto(id = 1L)

        val resultado = repository.actualizarSolicitud(fichaEjemplo)

        assertEquals(fichaEjemplo, resultado)
        coVerify(exactly = 1) { dao.actualizarSolicitud(fichaEjemplo) }
        coVerify(exactly = 1) { api.actualizarSolicitud(1L, any()) }
    }

    @Test
    fun actualizarSolicitud_caminoFeliz2_idCeroSoloActualizaRoom() = runTest {
        val local = fichaEjemplo.copy(id = 0)

        repository.actualizarSolicitud(local)

        coVerify(exactly = 1) { dao.actualizarSolicitud(local) }
        coVerify(exactly = 0) { api.actualizarSolicitud(any(), any()) }
    }

    @Test
    fun actualizarSolicitud_caminoMalo_errorPutNoRompeYConservaCambioEnRoom() = runTest {
        coEvery { api.actualizarSolicitud(1L, any()) } throws RuntimeException("Error 500")

        val resultado = repository.actualizarSolicitud(fichaEjemplo)

        assertEquals(fichaEjemplo, resultado)
        coVerify(exactly = 1) { dao.actualizarSolicitud(fichaEjemplo) }
    }

    // --- eliminarSolicitud (2 felices, 1 malo) ---

    @Test
    fun eliminarSolicitud_caminoFeliz1_borraEnServidorYRoom() = runTest {
        coEvery { api.eliminarSolicitud(1L) } returns Response.success(Unit)

        val borradaEnServidor = repository.eliminarSolicitud(fichaEjemplo)

        assertTrue(borradaEnServidor)
        coVerify(exactly = 1) { dao.eliminarSolicitud(fichaEjemplo) }
    }

    @Test
    fun eliminarSolicitud_caminoFeliz2_idCeroSoloEliminaEnRoom() = runTest {
        val local = fichaEjemplo.copy(id = 0)

        val borradaEnServidor = repository.eliminarSolicitud(local)

        assertFalse(borradaEnServidor)
        coVerify(exactly = 0) { api.eliminarSolicitud(any()) }
        coVerify(exactly = 1) { dao.eliminarSolicitud(local) }
    }

    @Test
    fun eliminarSolicitud_caminoMalo_errorDeRedRetornaFalsePeroEliminaEnRoom() = runTest {
        coEvery { api.eliminarSolicitud(1L) } throws RuntimeException("Sin red")

        val borradaEnServidor = repository.eliminarSolicitud(fichaEjemplo)

        assertFalse(borradaEnServidor)
        coVerify(exactly = 1) { dao.eliminarSolicitud(fichaEjemplo) }
    }

    // --- sincronizarPendientes (2 felices, 1 malo) ---

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

    @Test
    fun sincronizarPendientes_caminoFeliz2_sinPendientesRetornaCero() = runTest {
        coEvery { dao.obtenerPendientes() } returns emptyList()

        val enviadas = repository.sincronizarPendientes()

        assertEquals(0, enviadas)
        coVerify(exactly = 0) { api.crearSolicitud(any()) }
    }

    @Test
    fun sincronizarPendientes_caminoMalo_servidorApagadoNoApruebaNinguna() = runTest {
        val p1 = fichaEjemplo.copy(id = 1, estadoEnvio = "PENDIENTE SIN RED")
        coEvery { dao.obtenerPendientes() } returns listOf(p1)
        coEvery { api.crearSolicitud(any()) } throws RuntimeException("Servidor apagado")

        val enviadas = repository.sincronizarPendientes()

        assertEquals(0, enviadas)
        coVerify(exactly = 0) { dao.eliminarSolicitud(any()) }
        coVerify(exactly = 0) { dao.sincronizarPendientes() }
    }
}
