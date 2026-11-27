package com.duoc.corpaliv.data

// archivo de pruebas unitarias para MindicadorRepository (api externa mindicador.cl)
// usa mockk para simular la interfaz MindicadorApiService sin depender de internet real
// usa runTest para ejecutar funciones suspend de corrutinas de forma determinista

import com.duoc.corpaliv.remote.IndicadorItemDto
import com.duoc.corpaliv.remote.MindicadorApiService
import com.duoc.corpaliv.remote.MindicadorResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class MindicadorRepositoryTest {

    private lateinit var api: MindicadorApiService
    private lateinit var repository: MindicadorRepository

    // antes de cada test se crea un mock limpio de la api y se inyecta al repositorio
    @Before
    fun configurar() {
        api = mockk()
        repository = MindicadorRepository(api)
    }

    // --- obtenerResumen con datos ---

    // simula respuesta completa de mindicador.cl y espera uf, utm, dolar y fecha recortada a yyyy-mm-dd
    @Test
    fun obtenerResumen_caminoFeliz1_retornaUfUtmDolarYFechaCorta() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11-26T03:00:00.000Z",
            uf = IndicadorItemDto("uf", "UF", 38512.4),
            utm = IndicadorItemDto("utm", "UTM", 67294.0),
            dolar = IndicadorItemDto("dolar", "Dolar", 948.7)
        )

        val resumen = repository.obtenerResumen()

        assertEquals(38512.4, resumen.uf, 0.01)
        assertEquals(67294.0, resumen.utm, 0.01)
        assertEquals(948.7, resumen.dolar, 0.01)
        assertEquals("2026-11-26", resumen.fecha)
    }

    // simula respuesta parcial donde solo llega la uf y espera 0.0 por defecto en utm y dolar
    @Test
    fun obtenerResumen_caminoFeliz2_aceptaRespuestaParcialConUfValida() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11-26",
            uf = IndicadorItemDto("uf", "UF", 38500.0),
            utm = null,
            dolar = null
        )

        val resumen = repository.obtenerResumen()

        assertEquals(38500.0, resumen.uf, 0.01)
        assertEquals(0.0, resumen.utm, 0.01)
        assertEquals(0.0, resumen.dolar, 0.01)
    }

    // simula respuesta vacia sin indicadores y espera que lance IllegalStateException
    @Test
    fun obtenerResumen_caminoMalo_valoresVaciosOCeroLanzaExcepcion() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse()

        try {
            repository.obtenerResumen()
            fail("Se esperaba IllegalStateException cuando no hay valores validos")
        } catch (e: IllegalStateException) {
            assertEquals("La API no devolvio valores validos", e.message)
        }
    }

    // --- manejo de errores de red y formatos ---

    // simula una fecha ya corta (menor a 10 caracteres) y espera que no falle al recortar
    @Test
    fun obtenerResumen_caminoFeliz3_fechaCortaSinTruncar() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11",
            uf = IndicadorItemDto("uf", "UF", 38600.0)
        )

        val resumen = repository.obtenerResumen()
        assertEquals("2026-11", resumen.fecha)
    }

    // simula solo el dolar con decimales y espera que el valor se conserve con precision
    @Test
    fun obtenerResumen_caminoFeliz4_valoresDecimalesExactos() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11-26",
            dolar = IndicadorItemDto("dolar", "Dolar", 955.25)
        )

        val resumen = repository.obtenerResumen()
        assertEquals(955.25, resumen.dolar, 0.001)
    }

    // simula caida o timeout de red en la api y espera que la excepcion se propague al viewmodel
    @Test
    fun obtenerResumen_caminoMalo2_errorDeRedSePropaga() = runTest {
        coEvery { api.obtenerIndicadores() } throws RuntimeException("Timeout mindicador")

        try {
            repository.obtenerResumen()
            fail("Se esperaba RuntimeException por error de red")
        } catch (e: RuntimeException) {
            assertEquals("Timeout mindicador", e.message)
        }
    }
}
