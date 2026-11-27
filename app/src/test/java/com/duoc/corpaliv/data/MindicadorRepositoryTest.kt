package com.duoc.corpaliv.data

// pruebas unitarias de MindicadorRepository con mockk y corrutinas
// valida 2 caminos felices y 1 camino malo por escenario

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

    @Before
    fun configurar() {
        api = mockk()
        repository = MindicadorRepository(api)
    }

    // --- obtenerResumen con datos (2 felices, 1 malo) ---

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

    // --- manejo de errores de red (2 felices, 1 malo) ---

    @Test
    fun obtenerResumen_caminoFeliz3_fechaCortaSinTruncar() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11",
            uf = IndicadorItemDto("uf", "UF", 38600.0)
        )

        val resumen = repository.obtenerResumen()
        assertEquals("2026-11", resumen.fecha)
    }

    @Test
    fun obtenerResumen_caminoFeliz4_valoresDecimalesExactos() = runTest {
        coEvery { api.obtenerIndicadores() } returns MindicadorResponse(
            fecha = "2026-11-26",
            dolar = IndicadorItemDto("dolar", "Dolar", 955.25)
        )

        val resumen = repository.obtenerResumen()
        assertEquals(955.25, resumen.dolar, 0.001)
    }

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
