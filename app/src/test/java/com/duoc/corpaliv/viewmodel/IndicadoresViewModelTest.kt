package com.duoc.corpaliv.viewmodel

// pruebas unitarias de IndicadoresViewModel controlando el reloj de corrutinas
// cubre carga inicial y recarga manual con 2 caminos felices y 1 camino malo cada uno

import com.duoc.corpaliv.data.IndicadoresResumen
import com.duoc.corpaliv.data.MindicadorRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: MindicadorRepository

    private val resumenEjemplo = IndicadoresResumen(
        uf = 38500.0,
        utm = 67294.0,
        dolar = 950.0,
        fecha = "2026-11-26"
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // --- carga inicial (2 felices, 1 malo) ---

    @Test
    fun init_caminoFeliz1_cargaIndicadoresYApagaCargando() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertFalse(vm.estado.value.cargando)
        assertEquals(resumenEjemplo, vm.estado.value.resumen)
        assertFalse(vm.estado.value.esError)
    }

    @Test
    fun init_caminoFeliz2_cargaAutomaticaNoMuestraBannerManual() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertNull(vm.estado.value.mensajeEstado)
    }

    @Test
    fun init_caminoMalo_sinRedDejaErrorVisibleYSinResumen() = runTest {
        coEvery { repository.obtenerResumen() } throws RuntimeException("Sin internet")

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertFalse(vm.estado.value.cargando)
        assertNull(vm.estado.value.resumen)
        assertTrue(vm.estado.value.esError)
        assertEquals("Sin conexión para consultar mindicador.cl", vm.estado.value.mensajeEstado)
    }

    // --- recarga manual (2 felices, 1 malo) ---

    @Test
    fun cargarIndicadores_caminoFeliz1_muestraAvisoVerdeAlActualizar() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        vm.cargarIndicadores(manual = true)
        advanceTimeBy(400)

        assertEquals("Indicadores actualizados", vm.estado.value.mensajeEstado)
        assertFalse(vm.estado.value.esError)
        advanceUntilIdle()
    }

    @Test
    fun cargarIndicadores_caminoFeliz2_avisoVerdeSeOcultaTras2500ms() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        vm.cargarIndicadores(manual = true)
        advanceUntilIdle()

        assertNull(vm.estado.value.mensajeEstado)
        assertNotNull(vm.estado.value.resumen)
    }

    @Test
    fun cargarIndicadores_caminoMalo_enModoAvionConservaUltimosValoresYAvisa() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        coEvery { repository.obtenerResumen() } throws RuntimeException("Modo avion")
        vm.cargarIndicadores(manual = true)
        advanceTimeBy(400)

        assertTrue(vm.estado.value.esError)
        assertEquals(resumenEjemplo, vm.estado.value.resumen)
        assertEquals("Sin conexión · mostrando últimos valores", vm.estado.value.mensajeEstado)
        advanceUntilIdle()
    }
}
